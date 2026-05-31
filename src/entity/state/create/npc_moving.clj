(ns entity.state.create.npc-moving
  (:require [game.state :as state]
            [game.constants :refer [reaction-time-multiplier]]
            [moon.stats :as stats]
            [moon.timer :as timer]))

(defmethod state/create :npc-moving
  [[_k movement-vector] eid {:keys [ctx/elapsed-time]}]
  {:movement-vector movement-vector
   :timer (timer/create elapsed-time
                        (* (stats/get-stat-value (:entity/stats @eid) :stats/reaction-time)
                           reaction-time-multiplier))})
