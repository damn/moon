(ns entity.state.create.npc-moving
  (:require [game.state :as state]
            [game.constants :refer [reaction-time-multiplier]]
            [moon.stats.get-stat-value :refer [get-stat-value]]
            [clojure.timer.create :refer [create-timer]]))

(defmethod state/create :npc-moving
  [[_k movement-vector] eid {:keys [ctx/elapsed-time]}]
  {:movement-vector movement-vector
   :timer (create-timer elapsed-time
                        (* (get-stat-value (:entity/stats @eid) :stats/reaction-time)
                           reaction-time-multiplier))})
