(ns moon.entity.image
  (:require [moon.textures :as textures]))

(defn draw-image
  [image
   {:keys [entity/body]}
   {:keys [ctx/textures]}]
  [[:draw/texture-region
    (textures/texture-region textures image)
    (:body/position body)
    {:center? true
     :rotation (or (:body/rotation-angle body)
                   0)}]])

(defn render
  [[_k image] entity ctx]
  (draw-image image entity ctx))
