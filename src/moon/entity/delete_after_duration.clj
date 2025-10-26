(ns moon.entity.delete-after-duration
  (:require [moon.timer :as timer]))

(defn create [duration {:keys [world/elapsed-time]}]
  (timer/create elapsed-time duration))
