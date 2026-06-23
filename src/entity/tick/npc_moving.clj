(ns entity.tick.npc-moving
  (:require [timer.stopped :refer [stopped?]]))

(defn f
  [{:keys [timer]} eid {:keys [ctx/elapsed-time]}]
  (when (stopped? elapsed-time timer)
    [[:tx/event eid :timer-finished]]))
