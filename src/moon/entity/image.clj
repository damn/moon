(ns moon.entity.image
  (:require [moon.entity :as entity]
            [moon.graphics :as graphics]))

(defn draw-image
  [image
   {:keys [entity/body]}
   {:keys [ctx/graphics]}]
  [[:draw/texture-region
    (graphics/texture-region graphics image)
    (:body/position body)
    {:center? true
     :rotation (or (:body/rotation-angle body)
                   0)}]])

(defmethod entity/render :entity/image
  [[_k image] entity ctx]
  (draw-image image entity ctx))
