(ns ctx.entity.tick.npc-moving
  (:require [clojure.timer.stopped :refer [stopped?]]))

(defn f
  [{:keys [timer]} eid {:keys [ctx/elapsed-time]}]
  (when (stopped? elapsed-time timer)
    [[:tx/event eid :timer-finished]]))
