(ns entity.tick.stunned
  (:require [timer.stopped :refer [stopped?]]))

(defn f
  [{:keys [counter]} eid {:keys [ctx/elapsed-time]}]
  (when (stopped? elapsed-time counter)
    [[:tx/event eid :effect-wears-off]]))
