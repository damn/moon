(ns moon.textures
  (:require [gdx.graphics.texture :as texture]))

(defn texture-region [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [texture (get textures file)]
    (if-let [[x y w h] bounds]
      (texture/region texture x y w h)
      (texture/region texture))))
