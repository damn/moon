(ns moon.entity.animation
  (:require [moon.animation]
            [moon.entity :as entity]))

(defmethod entity/create :entity/animation
  [[_ v] world]
  (moon.animation/create v nil))
