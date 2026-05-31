(ns entity.tick.stunned
  (:require [game.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/tick :stunned
  [[_k {:keys [counter]}] eid {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/event eid :effect-wears-off]]))
