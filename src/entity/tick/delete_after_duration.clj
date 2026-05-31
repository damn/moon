(ns entity.tick.delete-after-duration
  (:require [game.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/tick :entity/delete-after-duration
  [[_k counter] eid {:keys [ctx/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/mark-destroyed eid]]))
