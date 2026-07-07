(ns clojure.tx.spawn-alert
  (:require [clojure.timer-create :refer [create-timer]]))

(defn do!
  [{:keys [ctx/elapsed-time]} position faction duration]
  [[:tx/spawn-effect
    position
    {:entity/alert-friendlies-after-duration
     {:counter (create-timer elapsed-time duration)
      :faction faction}}]])
