(ns moon.textures
  (:require [texture.texture-region :as texture-region]))

(defn texture-region [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [texture (get textures file)]
    (if-let [[x y w h] bounds]
      (texture-region/f texture x y w h)
      (texture-region/f texture))))
