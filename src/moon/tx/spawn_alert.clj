(ns moon.tx.spawn-alert
  (:require [moon.timer :as timer]))

(defn do!
  [{:keys [ctx/world]} position faction duration]
  [[:tx/spawn-effect
    position
    {:entity/alert-friendlies-after-duration
     {:counter (timer/create (:world/elapsed-time world) duration)
      :faction faction}}]])
