(ns moon.entity.animation
  (:require [moon.animation :as animation]
            [moon.entity :as entity]
            [moon.entity.image :as image]))

(defmethod entity/create :entity/animation
  [[_ v] _world]
  (animation/create v nil))

(defmethod entity/tick :entity/animation
  [[_k animation] eid {:keys [world/delta-time]}]
  [[:tx/assoc eid :entity/animation (animation/tick animation delta-time)]
   (when (and (:delete-after-stopped? animation)
              (animation/stopped? animation))
     [:tx/mark-destroyed eid])])

(defmethod entity/render :entity/animation
  [[_k animation] entity ctx]
  (image/draw-image (animation/current-frame animation)
                    entity
                    ctx))
