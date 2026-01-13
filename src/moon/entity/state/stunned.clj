(ns moon.entity.state.stunned
  (:require [moon.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/tick :stunned
  [[_k {:keys [counter]}] eid {:keys [world/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/event eid :effect-wears-off]]))
