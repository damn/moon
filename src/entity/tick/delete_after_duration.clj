(ns entity.tick.delete-after-duration
  (:require [moon.timer :as timer]))

(defn f
  [counter eid {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/mark-destroyed eid]]))
