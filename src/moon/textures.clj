(ns moon.textures
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn texture-region [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [texture (get textures file)]
    (if-let [[x y w h] bounds]
      (TextureRegion. texture (int x) (int y) (int w) (int h))
      (TextureRegion. texture))))
