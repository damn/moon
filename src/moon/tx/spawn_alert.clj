(ns moon.tx.spawn-alert
  (:require [moon.timer :as timer]))

(defn do!
  [{:keys [ctx/elapsed-time]} position faction duration]
  [[:tx/spawn-effect
    position
    {:entity/alert-friendlies-after-duration
     {:counter (timer/create elapsed-time duration)
      :faction faction}}]])
