(ns moon.textures
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]))

(defn texture-region [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [texture (get textures file)]
    (if-let [[x y w h] bounds]
      (texture-region/create texture x y w h)
      (texture-region/create texture))))
