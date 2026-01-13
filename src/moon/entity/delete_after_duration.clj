(ns moon.entity.delete-after-duration
  (:require [moon.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/create :entity/delete-after-duration
  [[_ duration] {:keys [world/elapsed-time]}]
  (timer/create elapsed-time duration))
