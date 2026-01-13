(ns moon.entity.projectile-collision
  (:require [moon.entity :as entity]))

(defmethod entity/create :entity/projectile-collision
  [[_ v] _world]
  (assoc v :already-hit-bodies #{}))

