(ns ctx.entity.state.create.npc-moving
  (:require [moon.stats.get-stat-value :refer [get-stat-value]]
            [clojure.timer-create :refer [create-timer]]))

(defn f
  [[_k movement-vector] eid {:keys [ctx/elapsed-time]}]
  {:movement-vector movement-vector
   :timer (create-timer elapsed-time
                        (* (get-stat-value (:entity/stats @eid) :stats/reaction-time)
                           0.016))})
