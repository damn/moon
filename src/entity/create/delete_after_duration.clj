(ns entity.create.delete-after-duration
  (:require [game.entity :as entity]
            [moon.timer :as timer]))

(defmethod entity/create :entity/delete-after-duration
  [[_ duration] {:keys [ctx/elapsed-time]}]
  (timer/create elapsed-time duration))
