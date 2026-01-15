(ns moon.entity.delete-after-duration
  (:require [moon.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/create :entity/delete-after-duration
  [[_ duration] {:keys [ctx/world]}]
  (timer/create (:world/elapsed-time world) duration))

(defmethod entity/tick :entity/delete-after-duration
  [[_k counter] eid {:keys [world/elapsed-time]}]
  (when (timer/stopped? elapsed-time counter)
    [[:tx/mark-destroyed eid]]))
