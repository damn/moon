(ns clojure.k-tick.npc-moving
  (:require [clojure.stopped :refer [stopped?]]))

(defn f
  [{:keys [timer]} eid {:keys [ctx/elapsed-time]}]
  (when (stopped? elapsed-time timer)
    [[:tx/event eid :timer-finished]]))
