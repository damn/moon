(ns entity.tick.delete-after-duration
  (:require [timer.stopped :refer [stopped?]]))

(defn f
  [counter eid {:keys [ctx/elapsed-time]}]
  (when (stopped? elapsed-time counter)
    [[:tx/mark-destroyed eid]]))
