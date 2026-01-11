(ns moon.world.tick-entities
  (:require [clojure.grid2d :as g2d]
            [clojure.math :as math]
            [clojure.math.vector2 :as v]
            [moon.effect :as effect]
            [moon.animation :as animation]
            [moon.body :as body]
            [moon.skill :as skill]
            [moon.entity.stats :as stats]
            [moon.timer :as timer]
            [moon.utils :as utils]
            [moon.world.grid :as grid]
            [moon.world.grid.cell :as cell]
            [moon.world.potential-fields-movement :as potential-fields-movement]
            [moon.world.raycaster :as raycaster]))

(defn- move-position [position {:keys [direction speed delta-time]}]
  (mapv #(+ %1 (* %2 speed delta-time)) position direction))

(defn- move-body [body movement]
  (update body :body/position move-position movement))

(defn- try-move [grid body entity-id movement]
  (let [new-body (move-body body movement)]
    (when (grid/valid-position? grid new-body entity-id)
      new-body)))

(defn- try-move-solid-body [grid body entity-id {[vx vy] :direction :as movement}]
  (let [xdir (math/signum (float vx))
        ydir (math/signum (float vy))]
    (or (try-move grid body entity-id movement)
        (try-move grid body entity-id (assoc movement :direction [xdir 0]))
        (try-move grid body entity-id (assoc movement :direction [0 ydir])))))

(defn- tick-entities!*
  [{:keys [world/active-entities]
    :as world}
   k->tick-fn]
  (mapcat (fn [eid]
            (mapcat (fn [[k v]]
                      (try (when-let [f (k->tick-fn k)]
                             (f v eid world))
                           (catch Throwable t
                             (throw (ex-info "" {:eid eid} t)))))
                    @eid))
          active-entities))

(comment
 (= (tick-entities!* {:world/active-entities [(atom {:firstk :foo
                                                     :secondk :bar})
                                              (atom {:firstk2 :foo2
                                                     :secondk2 :bar2})]}
                     {:firstk (fn [v eid world]
                                [[:foo/bar]])
                      :secondk (fn [v eid world]
                                 [[:foo/barz]
                                  [:asdf]])
                      :firstk2 (fn [v eid world]
                                 nil)
                      :secondk2 (fn [v eid world]
                                  [[:asdf2] [:asdf3]])})
    [[:foo/bar]
     [:foo/barz]
     [:asdf]
     [:asdf2]
     [:asdf3]])
 )

(defn- npc-effect-ctx
  [{:keys [world/grid]
    :as world}
   eid]
  (let [entity @eid
        target (grid/nearest-enemy grid entity)
        target (when (and target
                          (raycaster/line-of-sight? world entity @target))
                 target)]
    {:effect/source eid
     :effect/target target
     :effect/target-direction (when target
                                (body/direction (:entity/body entity)
                                                (:entity/body @target)))}))

(defn- npc-choose-skill [world entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (skill/usable-state % entity effect-ctx))
                     (->> (:skill/effects %)
                          (filter (fn [e] (effect/applicable? e effect-ctx)))
                          (some (fn [e] (effect/useful? e effect-ctx world))))))
       first))

(defn- update-effect-ctx
  [world {:keys [effect/source effect/target] :as effect-ctx}]
  (if (and target
           (not (:entity/destroyed? @target))
           (raycaster/line-of-sight? world @source @target))
    effect-ctx
    (dissoc effect-ctx :effect/target)))

(def ^:private k->tick-fn
  {:entity/alert-friendlies-after-duration (fn
                                             [{:keys [counter faction]}
                                              eid
                                              {:keys [world/elapsed-time
                                                      world/grid]}]
                                             (when (timer/stopped? elapsed-time counter)
                                               (cons [:tx/mark-destroyed eid]
                                                     (for [friendly-eid (->> {:position (:body/position (:entity/body @eid))
                                                                              :radius 4}
                                                                             (grid/circle->entities grid)
                                                                             (filter #(= (:entity/faction @%) faction)))]
                                                       [:tx/event friendly-eid :alert]))))

   :entity/animation                       (fn [animation eid {:keys [world/delta-time]}]
                                             [[:tx/assoc eid :entity/animation (animation/tick animation delta-time)]
                                              (when (and (:delete-after-stopped? animation)
                                                         (animation/stopped? animation))
                                                [:tx/mark-destroyed eid])])

   :entity/delete-after-duration           (fn [counter eid {:keys [world/elapsed-time]}]
                                             (when (timer/stopped? elapsed-time counter)
                                               [[:tx/mark-destroyed eid]]))

   :entity/movement                        (fn
                                             [{:keys [direction
                                                      speed
                                                      rotate-in-movement-direction?]
                                               :as movement}
                                              eid
                                              {:keys [world/delta-time
                                                      world/grid
                                                      world/max-speed]}]
                                             (assert (<= 0 speed max-speed)
                                                     (pr-str speed))
                                             (assert (or (zero? (v/length direction))
                                                         (utils/nearly-equal? 1 (v/length direction)))
                                                     (str "cannot understand direction: " (pr-str direction)))
                                             (when-not (or (zero? (v/length direction))
                                                           (nil? speed)
                                                           (zero? speed))
                                               (let [movement (assoc movement :delta-time delta-time)
                                                     body (:entity/body @eid)]
                                                 (when-let [body (if (:body/collides? body)
                                                                   (try-move-solid-body grid body (:entity/id @eid) movement)
                                                                   (move-body body movement))]
                                                   [[:tx/move-entity eid body direction rotate-in-movement-direction?]]))))

   :entity/projectile-collision            (fn
                                             [{:keys [entity-effects already-hit-bodies piercing?]}
                                              eid
                                              {:keys [world/grid]}]
                                             (let [entity @eid
                                                   cells* (map deref (g2d/get-cells grid (body/touched-tiles (:entity/body entity))))
                                                   hit-entity (first (filter #(and (not (contains? already-hit-bodies %))
                                                                                   (not= (:entity/faction entity)
                                                                                         (:entity/faction @%))
                                                                                   (:body/collides? (:entity/body @%))
                                                                                   (body/overlaps? (:entity/body entity)
                                                                                                   (:entity/body @%)))
                                                                             (grid/cells->entities cells*)))
                                                   destroy? (or (and hit-entity (not piercing?))
                                                                (some #(cell/blocked? % (:body/z-order (:entity/body entity))) cells*))]
                                               [(when destroy?
                                                  [:tx/mark-destroyed eid])
                                                (when hit-entity
                                                  [:tx/assoc-in
                                                   eid
                                                   [:entity/projectile-collision
                                                    :already-hit-bodies]
                                                   (conj already-hit-bodies hit-entity)])
                                                (when hit-entity
                                                  [:tx/effect
                                                   {:effect/source eid
                                                    :effect/target hit-entity}
                                                   entity-effects])]))

   :entity/skills                          (fn [skills eid {:keys [world/elapsed-time]}]
                                             (for [{:keys [skill/cooling-down?] :as skill} (vals skills)
                                                   :when (and cooling-down?
                                                              (timer/stopped? elapsed-time cooling-down?))]
                                               [:tx/assoc-in eid [:entity/skills (:property/id skill) :skill/cooling-down?] false]))

   :active-skill                           (fn
                                             [{:keys [skill effect-ctx counter]}
                                              eid
                                              {:keys [world/elapsed-time]
                                               :as world}]
                                             (let [effect-ctx (update-effect-ctx world effect-ctx)]
                                               (cond
                                                (not (seq (filter #(effect/applicable? % effect-ctx)
                                                                  (:skill/effects skill))))
                                                [[:tx/event eid :action-done]]

                                                (timer/stopped? elapsed-time counter)
                                                [[:tx/effect effect-ctx (:skill/effects skill)]
                                                 [:tx/event eid :action-done]])))

   :npc-idle                               (fn [_ eid world]
                                             (let [effect-ctx (npc-effect-ctx world eid)]
                                               (if-let [skill (npc-choose-skill world @eid effect-ctx)]
                                                 [[:tx/event eid :start-action [skill effect-ctx]]]
                                                 [[:tx/event eid :movement-direction (or (potential-fields-movement/find-direction world eid)
                                                                                         [0 0])]])))

   :npc-moving                             (fn [{:keys [timer]} eid {:keys [world/elapsed-time]}]
                                             (when (timer/stopped? elapsed-time timer)
                                               [[:tx/event eid :timer-finished]]))

   :npc-sleeping                           (fn [_ eid {:keys [world/grid]}]
                                             (let [entity @eid]
                                               (when-let [distance (grid/nearest-enemy-distance grid entity)]
                                                 (when (<= distance (stats/get-stat-value (:entity/stats entity) :stats/aggro-range))
                                                   [[:tx/event eid :alert]]))))

   :stunned                                (fn [{:keys [counter]} eid {:keys [world/elapsed-time]}]
                                             (when (timer/stopped? elapsed-time counter)
                                               [[:tx/event eid :effect-wears-off]]))

   :entity/string-effect                   (fn
                                             [{:keys [counter]}
                                              eid
                                              {:keys [world/elapsed-time]}]
                                             (when (timer/stopped? elapsed-time counter)
                                               [[:tx/dissoc eid :entity/string-effect]]))

   :entity/temp-modifier                   (fn
                                             [{:keys [modifiers counter]}
                                              eid
                                              {:keys [world/elapsed-time]}]
                                             (when (timer/stopped? elapsed-time counter)
                                               [[:tx/dissoc eid :entity/temp-modifier]
                                                [:tx/update eid :entity/stats stats/remove-mods modifiers]]))})

(defn do! [world]
  (tick-entities!* world k->tick-fn))
