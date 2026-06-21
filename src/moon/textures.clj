(ns moon.textures
  (:require [com.badlogic.gdx.graphics.texture.region :as region]))

(defn texture-region [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [texture (get textures file)]
    (if-let [[x y w h] bounds]
      (region/f texture x y w h)
      (region/f texture))))
