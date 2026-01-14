(ns moon.entity.state.npc-moving
  (:require [moon.entity :as entity]
            [moon.entity.state :as state]
            [moon.entity.stats :as stats]
            [moon.timer :as timer]))

(def reaction-time-multiplier 0.016)

(defmethod state/create :npc-moving
  [[_k movement-vector] eid {:keys [world/elapsed-time]}]
  {:movement-vector movement-vector
   :timer (timer/create elapsed-time
                        (* (stats/get-stat-value (:entity/stats @eid) :stats/reaction-time)
                           reaction-time-multiplier))})

(defmethod entity/tick :npc-moving
  [[_k {:keys [timer]}] eid {:keys [world/elapsed-time]}]
  (when (timer/stopped? elapsed-time timer)
    [[:tx/event eid :timer-finished]]))

(defmethod state/enter :npc-moving
  [[_k {:keys [movement-vector]}] eid]
  [[:tx/assoc eid :entity/movement {:direction movement-vector
                                    :speed (or (stats/get-stat-value (:entity/stats @eid) :stats/movement-speed)
                                               0)}]])

(defmethod state/exit :npc-moving
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])
