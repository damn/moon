(ns entity.tick.string-effect
  (:require [game.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/tick :entity/string-effect
  [[_k {:keys [counter]}]
   eid
   {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/dissoc eid :entity/string-effect]]))
