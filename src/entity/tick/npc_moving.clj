(ns entity.tick.npc-moving
  (:require [game.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/tick :npc-moving
  [[_k {:keys [timer]}] eid {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time timer)
    [[:tx/event eid :timer-finished]]))
