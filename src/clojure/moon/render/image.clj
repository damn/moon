(ns clojure.moon.render.image
  (:require [clojure.moon-textures :as textures]))

(defn f
  [image {:keys [entity/body]} {:keys [ctx/textures]}]
  [[:draw/texture-region
    (textures/texture-region textures image)
    (:body/position body)
    {:center? true
     :rotation (or (:body/rotation-angle body)
                   0)}]])
