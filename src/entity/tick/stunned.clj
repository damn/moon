(ns entity.tick.stunned
  (:require [moon.timer :as timer]))

(defn f
  [[_k {:keys [counter]}] eid {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/event eid :effect-wears-off]]))
