(ns entity.create.delete-after-duration
  (:require [moon.timer :as timer]))

(defn f
  [duration {:keys [ctx/elapsed-time]}]
  (timer/create elapsed-time duration))
