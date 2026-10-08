(ns game.listener.render.tick-world.tick-component
  (:require [effect.applicable :refer [applicable?]]
            [effect.useful :refer [useful?]]
            [game.fsm :refer [handle-fsm-event!]]
            [moon.body :as body]
            [moon.cell :as cell]
            [moon.grid :as grid]
            [grid.find-direction :refer [find-direction]]
            [moon.number :as number]
            [moon.potential-field :as potential-field]
            [moon.raycaster :as raycaster]
            [moon.stats :as stats]
            [moon.timer :as timer]
            [moon.v2 :as v2]
            [skill.usable-state :refer [usable-state]]
            [world.relocate-eid :refer [relocate-eid!]]))

(defn- choose-skill [ray-blocked? entity effect-ctx]
  (->> entity
       :entity/skills
       vals
       (sort-by :skill/cost)
       reverse
       (filter #(and (= :usable (usable-state % entity effect-ctx))
                     (->> (:skill/effects %)
                          (filter (fn [e] (applicable? e effect-ctx)))
                          (some (fn [e] (useful? e effect-ctx ray-blocked?))))))
       first))

(defn- create-effect-ctx
  [world raycaster ctx eid]
  (let [world @world
        raycaster @raycaster
        entity @eid
        target (potential-field/nearest-enemy (:world/grid world) entity)
        target (when (and target
                          (raycaster/line-of-sight? raycaster entity @target))
                 target)]
    {:effect/source eid
     :effect/target target
     :effect/target-direction (when target
                                (body/direction entity
                                                @target))}))

(defn- update-effect-ctx
  [raycaster effect-ctx]
  (let [source (:effect/source effect-ctx)
        target (:effect/target effect-ctx)]
    (if (and target
             (not (:entity/destroyed? @target))
             (raycaster/line-of-sight? raycaster @source @target))
      effect-ctx
      (dissoc effect-ctx :effect/target))))

(defn tick-component
  [world raycaster elapsed-time delta-time audio skin stage textures z-orders
   minimum-size max-speed
   ctx world-mouse-position apply-effects! eid [k v]]
  (let [elapsed-time* @elapsed-time]
    (case k
      :entity/animation
      (let [{:keys [delete-after-stopped?
                    looping?
                    cnt
                    maxcnt]
             :as animation} v]
        (swap! eid assoc :entity/animation (let [maxcnt (float maxcnt)
                                                 newcnt (+ (float cnt) (float @delta-time))]
                                             (assoc animation :cnt (cond (< newcnt maxcnt) newcnt
                                                                         looping? (min maxcnt (- newcnt maxcnt))
                                                                         :else maxcnt))))
        (when (and delete-after-stopped?
                   (and (not looping?) (>= cnt maxcnt)))
          (swap! eid assoc :entity/destroyed? true))
        nil)

      :entity/alert-friendlies-after-duration
      (let [{:keys [counter faction]} v]
        (when (timer/stopped? elapsed-time* counter)
          (swap! eid assoc :entity/destroyed? true)
          (doseq [friendly-eid (->> {:position (:entity/position @eid)
                                     :radius 4}
                                    (grid/circle->entities (:world/grid @world))
                                    (filter #(= (:entity/faction @%) faction)))]
            (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size friendly-eid world-mouse-position :alert)))
        nil)

      :entity/string-effect
      (let [{:keys [counter]} v]
        (when (timer/stopped? elapsed-time* counter)
          (swap! eid dissoc :entity/string-effect))
        nil)

      :entity/skills
      (do (doseq [{:keys [skill/cooling-down?] :as skill} (vals v)
                  :when (and cooling-down?
                             (timer/stopped? elapsed-time* cooling-down?))]
            (swap! eid assoc-in [:entity/skills (:property/id skill) :skill/cooling-down?] false))
          nil)

      :entity/temp-modifier
      (let [{:keys [modifiers counter]} v]
        (when (timer/stopped? elapsed-time* counter)
          (swap! eid dissoc :entity/temp-modifier)
          (swap! eid update :entity/stats stats/remove-mods modifiers))
        nil)

      :entity/projectile-collision
      (let [{:keys [entity-effects already-hit-bodies piercing?]} v
            world* @world
            entity @eid
            touched-cells (map deref (keep (:world/grid world*) (body/touched-tiles entity)))
            hit-entity (first (filter #(and (not (contains? already-hit-bodies %))
                                            (not= (:entity/faction entity)
                                                  (:entity/faction @%))
                                            (:entity/collides? @%)
                                            (body/overlaps? entity
                                                            @%))
                                      (grid/entities touched-cells)))
            destroy? (or (and hit-entity (not piercing?))
                         (some #(cell/blocked? % (:entity/z-order entity)) touched-cells))]
        (when hit-entity
          (swap! eid assoc-in [:entity/projectile-collision :already-hit-bodies]
                 (conj already-hit-bodies hit-entity)))
        (when destroy?
          (swap! eid assoc :entity/destroyed? true))
        (when hit-entity
          (apply-effects! {:effect/source eid
                           :effect/target hit-entity}
                          entity-effects))
        nil)

      :active-skill
      (let [{:keys [skill effect-ctx counter]} v
            effect-ctx (update-effect-ctx @raycaster effect-ctx)]
        (cond
         (not (seq (filter #(applicable? % effect-ctx)
                           (:skill/effects skill))))
         (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-position :action-done)

         (timer/stopped? elapsed-time* counter)
         (do (apply-effects! effect-ctx (:skill/effects skill))
             (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-position :action-done)
             nil)))

      :entity/delete-after-duration
      (do (when (timer/stopped? elapsed-time* v)
            (swap! eid assoc :entity/destroyed? true))
          nil)

      :stunned
      (let [{:keys [counter]} v]
        (when (timer/stopped? elapsed-time* counter)
          (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-position :effect-wears-off)))

      :npc-moving
      (let [{:keys [timer]} v]
        (when (timer/stopped? elapsed-time* timer)
          (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-position :timer-finished)))

      :npc-sleeping
      (let [entity @eid]
        (when-let [distance (potential-field/nearest-enemy-distance (:world/grid @world) entity)]
          (when (<= distance (stats/get-value (:entity/stats entity) :stats/aggro-range))
            (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-position :alert))))

      :npc-idle
      (let [effect-ctx (create-effect-ctx world raycaster ctx eid)]
        (if-let [skill (choose-skill (partial raycaster/blocked? @raycaster) @eid effect-ctx)]
          (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-position :start-action [skill effect-ctx])
          (handle-fsm-event! world elapsed-time audio skin stage textures z-orders minimum-size eid world-mouse-position :movement-direction (or (find-direction (:world/grid @world) eid)
                                                             [0 0]))))

      :entity/movement
      (let [{:keys [direction
                    speed
                    rotate-in-movement-direction?]
             :as movement} v]
        (assert (<= 0 speed max-speed)
                (pr-str speed))
        (assert (vector? direction))
        (assert (or (zero? (v2/length direction))
                    (number/nearly-equal? 1 (v2/length direction)))
                (str "cannot understand direction: " (pr-str direction)))
        (when-not (or (zero? (v2/length direction))
                      (nil? speed)
                      (zero? speed))
          (let [world* @world
                movement (assoc movement :delta-time @delta-time)
                body @eid]
            (when-let [body (if (:entity/collides? body)
                               (grid/try-move-solid-body (:world/grid world*) body (:entity/id @eid) movement)
                               (let [{:keys [direction speed delta-time]} movement]
                                 (update body :entity/position
                                         #(mapv (fn [p d] (+ p (* d speed delta-time))) % direction))))]
              (swap! eid assoc :entity/position (:entity/position body))
              (when rotate-in-movement-direction?
                (swap! eid assoc :entity/rotation-angle
                       (v2/angle-from-vector direction)))
              (relocate-eid! world* eid)
              nil))))

      nil)))
