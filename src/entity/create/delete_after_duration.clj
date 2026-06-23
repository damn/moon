(ns entity.create.delete-after-duration
  (:require [timer.create :refer [create-timer]]))

(defn f
  [duration {:keys [ctx/elapsed-time]}]
  (create-timer elapsed-time duration))
