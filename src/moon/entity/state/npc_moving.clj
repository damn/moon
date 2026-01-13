(ns moon.entity.state.npc-moving
  (:require [moon.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/tick :npc-moving
  [[_k {:keys [timer]}] eid {:keys [world/elapsed-time]}]
  (when (timer/stopped? elapsed-time timer)
    [[:tx/event eid :timer-finished]]))
