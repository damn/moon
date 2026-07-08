(ns clojure.k-tick
  (:require [clojure.v2.angle-from-vector :as angle-from-vector]
            [clojure.body-touched-tiles :refer [touched-tiles]]
            [clojure.cell-is-blocked :as blocked?]
            [clojure.cells-entities :as cells->entities]
            [clojure.choose-skill :as choose-skill]
            [clojure.circle-entities :refer [circle->entities]]
            [clojure.create-effect-ctx :as create-effect-ctx]
            [clojure.get-cells :refer [get-cells]]
            [clojure.get-stat-value :refer [get-stat-value]]
            [clojure.is-applicable :as applicable?]
            [clojure.is-nearly-equal :as nearly-equal?]
            [clojure.v2.length :as length]
            [clojure.move :as move]
            [clojure.nearest-enemy-distance :refer [nearest-enemy-distance]]
            [clojure.npc-pathing :as npc-pathing]
            [clojure.overlaps :refer [overlaps?]]
            [clojure.remove-mods :as remove-mods]
            [clojure.stopped :refer [stopped?]]
            [clojure.try-move-solid-body :as try-move-solid-body]
            [clojure.update-effect-ctx :as update-effect-ctx]))

(def k->tick
  {:entity/animation
   (fn [{:keys [delete-after-stopped?
                looping?
                cnt
                maxcnt]
         :as animation}
        eid
        {:keys [ctx/delta-time]}]
     [[:tx/assoc eid :entity/animation (let [maxcnt (float maxcnt)
                                               newcnt (+ (float cnt) (float delta-time))]
                                         (assoc animation :cnt (cond (< newcnt maxcnt) newcnt
                                                                     looping? (min maxcnt (- newcnt maxcnt))
                                                                     :else maxcnt)))]
      (when (and delete-after-stopped?
                 (and (not looping?) (>= cnt maxcnt)))
        [:tx/mark-destroyed eid])])

   :entity/alert-friendlies-after-duration
   (fn [{:keys [counter faction]}
        eid
        {:keys [ctx/elapsed-time
                ctx/grid]}]
     (when (stopped? elapsed-time counter)
       (cons [:tx/mark-destroyed eid]
             (for [friendly-eid (->> {:position (:body/position (:entity/body @eid))
                                      :radius 4}
                                     (circle->entities grid)
                                     (filter #(= (:entity/faction @%) faction)))]
               [:tx/event friendly-eid :alert]))))

   :entity/string-effect
   (fn [{:keys [counter]}
        eid
        {:keys [ctx/elapsed-time]}]
     (when (stopped? elapsed-time counter)
       [[:tx/dissoc eid :entity/string-effect]]))

   :entity/skills
   (fn [skills eid {:keys [ctx/elapsed-time]}]
     (for [{:keys [skill/cooling-down?] :as skill} (vals skills)
           :when (and cooling-down?
                      (stopped? elapsed-time cooling-down?))]
       [:tx/assoc-in eid [:entity/skills (:property/id skill) :skill/cooling-down?] false]))

   :entity/temp-modifier
   (fn [{:keys [modifiers counter]}
        eid
        {:keys [ctx/elapsed-time]}]
     (when (stopped? elapsed-time counter)
       [[:tx/dissoc eid :entity/temp-modifier]
        [:tx/update eid :entity/stats remove-mods/f modifiers]]))

   :entity/projectile-collision
   (fn [{:keys [entity-effects already-hit-bodies piercing?]}
        eid
        {:keys [ctx/grid]}]
     (let [entity @eid
           cells* (map deref (get-cells grid (touched-tiles (:entity/body entity))))
           hit-entity (first (filter #(and (not (contains? already-hit-bodies %))
                                           (not= (:entity/faction entity)
                                                 (:entity/faction @%))
                                           (:body/collides? (:entity/body @%))
                                           (overlaps? (:entity/body entity)
                                                      (:entity/body @%)))
                                     (cells->entities/f cells*)))
           destroy? (or (and hit-entity (not piercing?))
                        (some #(blocked?/f % (:body/z-order (:entity/body entity))) cells*))]
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

   :active-skill
   (fn [{:keys [skill effect-ctx counter]}
        eid
        {:keys [ctx/elapsed-time
                ctx/raycaster]}]
     (let [effect-ctx (update-effect-ctx/f raycaster effect-ctx)]
       (cond
        (not (seq (filter #(applicable?/f % effect-ctx)
                          (:skill/effects skill))))
        [[:tx/event eid :action-done]]

        (stopped? elapsed-time counter)
        [[:tx/effect effect-ctx (:skill/effects skill)]
         [:tx/event eid :action-done]])))

   :entity/delete-after-duration
   (fn [counter eid {:keys [ctx/elapsed-time]}]
     (when (stopped? elapsed-time counter)
       [[:tx/mark-destroyed eid]]))

   :stunned
   (fn [{:keys [counter]} eid {:keys [ctx/elapsed-time]}]
     (when (stopped? elapsed-time counter)
       [[:tx/event eid :effect-wears-off]]))

   :npc-moving
   (fn [{:keys [timer]} eid {:keys [ctx/elapsed-time]}]
     (when (stopped? elapsed-time timer)
       [[:tx/event eid :timer-finished]]))

   :npc-sleeping
   (fn [_ eid {:keys [ctx/grid]}]
     (let [entity @eid]
       (when-let [distance (nearest-enemy-distance grid entity)]
         (when (<= distance (get-stat-value (:entity/stats entity) :stats/aggro-range))
           [[:tx/event eid :alert]]))))

   :npc-idle
   (fn [_ eid ctx]
     (let [effect-ctx (create-effect-ctx/f ctx eid)]
       (if-let [skill (choose-skill/f ctx @eid effect-ctx)]
         [[:tx/event eid :start-action [skill effect-ctx]]]
         [[:tx/event eid :movement-direction (or (npc-pathing/find-direction (:ctx/grid ctx) eid)
                                                 [0 0])]])))

   :entity/movement
   (fn [{:keys [direction
                speed
                rotate-in-movement-direction?]
         :as movement}
        eid
        {:keys [ctx/delta-time
                ctx/grid
                ctx/max-speed]}]
     (assert (<= 0 speed max-speed)
             (pr-str speed))
     (assert (vector? direction))
     (assert (or (zero? (length/f direction))
                 (nearly-equal?/f 1 (length/f direction)))
             (str "cannot understand direction: " (pr-str direction)))
     (when-not (or (zero? (length/f direction))
                   (nil? speed)
                   (zero? speed))
       (let [movement (assoc movement :delta-time delta-time)
             body (:entity/body @eid)]
         (when-let [body (if (:body/collides? body)
                            (try-move-solid-body/f grid body (:entity/id @eid) movement)
                            (update body :body/position move/f movement))]
           [[:tx/assoc-in eid [:entity/body :body/position] (:body/position body)]
            (when rotate-in-movement-direction?
              [:tx/assoc-in eid [:entity/body :body/rotation-angle] (angle-from-vector/f direction)])
            [:tx/move-entity eid]]))))})
