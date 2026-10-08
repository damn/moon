(ns entity.spawn
  (:require [animation.create :as animation.create]
            [game.entity :refer [set-item]]
            [moon.g2d :as moon-g2d]
            [moon.inventory :as inventory]
            [moon.item :as item]
            [moon.stats :as stats]
            [moon.timer :as timer]
            [reduce-fsm :as fsm]
            [world.register-eid :refer [register-eid!]]))

(defn create-entity-state [[k v] eid elapsed-time]
  (case k
    :active-skill
    (let [[skill effect-ctx] v]
      {:skill skill
       :effect-ctx effect-ctx
       :counter (timer/create elapsed-time
                              (/ (:skill/action-time skill)
                                 (or (stats/get-value (:entity/stats @eid)
                                                      (:skill/action-time-modifier-key skill))
                                     1)))})

    :stunned
    {:counter (timer/create elapsed-time v)}

    :player-moving
    {:movement-vector v}

    :npc-moving
    {:movement-vector v
     :timer (timer/create elapsed-time
                          (* (stats/get-value (:entity/stats @eid) :stats/reaction-time)
                             0.016))}

    :player-item-on-cursor
    {:item v}

    v))

(def ^:private fsms
  {:npc (fsm/fsm-inc
          [[:npc-sleeping
            :kill -> :npc-dead
            :stun -> :stunned
            :alert -> :npc-idle]
           [:npc-idle
            :kill -> :npc-dead
            :stun -> :stunned
            :start-action -> :active-skill
            :movement-direction -> :npc-moving]
           [:npc-moving
            :kill -> :npc-dead
            :stun -> :stunned
            :timer-finished -> :npc-idle]
           [:active-skill
            :kill -> :npc-dead
            :stun -> :stunned
            :action-done -> :npc-idle]
           [:stunned
            :kill -> :npc-dead
            :effect-wears-off -> :npc-idle]
           [:npc-dead]])
   :player (fsm/fsm-inc
            [[:player-idle
              :kill -> :player-dead
              :stun -> :stunned
              :start-action -> :active-skill
              :pickup-item -> :player-item-on-cursor
              :movement-input -> :player-moving]
             [:player-moving
              :kill -> :player-dead
              :stun -> :stunned
              :no-movement-input -> :player-idle]
             [:active-skill
              :kill -> :player-dead
              :stun -> :stunned
              :action-done -> :player-idle]
             [:stunned
              :kill -> :player-dead
              :effect-wears-off -> :player-idle]
             [:player-item-on-cursor
              :kill -> :player-dead
              :stun -> :stunned
              :drop-item -> :player-idle
              :dropped-item -> :player-idle]
             [:player-dead]])})

(defn- create-fsm
  [fsm initial-state]
  (assoc ((case fsm
            :fsms/player (:player fsms)
            :fsms/npc (:npc fsms))
          initial-state
          nil)
         :state initial-state))

(defn after-create-component
  [elapsed-time eid [k v]]
  (case k
    :entity/fsm
    (let [{:keys [fsm initial-state]} v]
      (swap! eid assoc :entity/fsm (create-fsm fsm initial-state))
      (swap! eid assoc initial-state (create-entity-state [initial-state nil] eid elapsed-time))
      nil)

    :entity/skills
    (do
      (swap! eid assoc :entity/skills nil)
      (doseq [{:keys [property/id] :as skill} v]
        (assert (not (contains? (:entity/skills @eid) id)))
        (swap! eid update :entity/skills assoc id skill))
      nil)

    :entity/inventory
    (do
      (swap! eid assoc :entity/inventory (->> #:inventory.slot{:bag      [6 4]
                                                              :weapon   [1 1]
                                                              :shield   [1 1]
                                                              :helm     [1 1]
                                                              :chest    [1 1]
                                                              :leg      [1 1]
                                                              :glove    [1 1]
                                                              :boot     [1 1]
                                                              :cloak    [1 1]
                                                              :necklace [1 1]
                                                              :rings    [2 1]}
                                                 (map (fn [[slot [width height]]]
                                                        [slot (moon-g2d/create width height (constantly nil))]))
                                                 (into {})))
      (doseq [item v]
        (assert (item/valid? item))
        (let [[cell cell-item] (inventory/can-pickup-item? (:entity/inventory @eid) item)]
          (assert cell)
          (assert (nil? cell-item))
          (swap! eid set-item cell item)))
      nil)

    nil))

(defn spawn-entity! [world elapsed-time z-orders minimum-size entity]
  (let [elapsed-time* @elapsed-time
        entity (reduce (fn [m [k v]]
                         (assoc m k (case k
                                      :entity/animation
                                      (animation.create/create v)

                                      :entity/delete-after-duration
                                      (timer/create elapsed-time* v)

                                      :entity/projectile-collision
                                      (assoc v :already-hit-bodies #{})

                                      :entity/stats
                                      (-> v
                                          (update :stats/mana (fn [v] [v v]))
                                          (update :stats/hp   (fn [v] [v v])))

                                      v)))
                       {}
                       entity)
        entity (let [{:entity/keys [position width height collides? z-order rotation-angle]} entity]
                 (assert position)
                 (assert width)
                 (assert height)
                 (assert (>= width  (if collides? minimum-size 0)))
                 (assert (>= height (if collides? minimum-size 0)))
                 (assert (or (boolean? collides?) (nil? collides?)))
                 (assert ((set z-orders) z-order))
                 (assert (or (nil? rotation-angle)
                             (<= 0 rotation-angle 360)))
                 (assoc entity
                        :entity/position (mapv float position)
                        :entity/width (float width)
                        :entity/height (float height)
                        :entity/rotation-angle (or rotation-angle 0)))
        eid (atom entity)]
    (register-eid! @world eid)
    (doseq [component @eid]
      (after-create-component elapsed-time* eid component))
    eid))
