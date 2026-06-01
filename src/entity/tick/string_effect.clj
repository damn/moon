(ns entity.tick.string-effect
  (:require [moon.timer :as timer]))

(defn f
  [{:keys [counter]}
   eid
   {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/dissoc eid :entity/string-effect]]))
