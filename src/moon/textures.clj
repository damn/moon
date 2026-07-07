(ns moon.textures
  (:require [clojure.texture-region :as texture-region]
            [clojure.texture :as texture]))

(defn texture-region
  [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [texture (get textures file)]
    (if-let [[x y w h] bounds]
      (texture-region/new texture x y w h)
      (texture-region/new texture))))
