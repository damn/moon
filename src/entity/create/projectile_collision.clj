(ns entity.create.projectile-collision
  (:require [game.entity :as entity]))

(defmethod entity/create :entity/projectile-collision
  [[_ v] _ctx]
  (assoc v :already-hit-bodies #{}))
