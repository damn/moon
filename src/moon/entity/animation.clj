(ns moon.entity.animation
  (:require [moon.animation :as animation]
            [moon.entity :as entity]))

(defmethod entity/create :entity/animation
  [[_ v] world]
  (animation/create v nil))

(defmethod entity/tick :entity/animation
  [[_k animation] eid {:keys [world/delta-time]}]
  [[:tx/assoc eid :entity/animation (animation/tick animation delta-time)]
   (when (and (:delete-after-stopped? animation)
              (animation/stopped? animation))
     [:tx/mark-destroyed eid])])
