(ns ctx.entity.tick.delete-after-duration
  (:require [clojure.stopped :refer [stopped?]]))

(defn f
  [counter eid {:keys [ctx/elapsed-time]}]
  (when (stopped? elapsed-time counter)
    [[:tx/mark-destroyed eid]]))
