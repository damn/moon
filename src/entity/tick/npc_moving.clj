(ns entity.tick.npc-moving
  (:require [moon.timer :as timer]))

(defn f
  [{:keys [timer]} eid {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time timer)
    [[:tx/event eid :timer-finished]]))
