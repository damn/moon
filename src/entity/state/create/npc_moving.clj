(ns entity.state.create.npc-moving
  (:require [game.state :as state]
            [moon.stats :as stats]
            [moon.timer :as timer]))

(def reaction-time-multiplier 0.016)

(defmethod state/create :npc-moving
  [[_k movement-vector] eid {:keys [ctx/elapsed-time]}]
  {:movement-vector movement-vector
   :timer (timer/create elapsed-time
                        (* (stats/get-stat-value (:entity/stats @eid) :stats/reaction-time)
                           reaction-time-multiplier))})
