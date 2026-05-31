(ns entity.render.animation
  (:require [game.entity :as entity]))

(defmethod entity/render :entity/animation
  [[_k {:keys [frames
               cnt
               frame-duration]}] entity ctx]
  (entity/render [:entity/image (frames (min (int (/ (float cnt) (float frame-duration)))
                                             (dec (count frames))))]
                 entity
                 ctx))
