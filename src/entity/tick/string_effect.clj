(ns entity.tick.string-effect
  (:require [clojure.timer.stopped :refer [stopped?]]))

(defn f
  [{:keys [counter]}
   eid
   {:keys [ctx/elapsed-time]}]
  (when (stopped? elapsed-time counter)
    [[:tx/dissoc eid :entity/string-effect]]))
