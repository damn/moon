(ns moon.textures
  (:require
            [com.badlogic.gdx.graphics.texture :as texture] [clojure.gdx.texture-region.new :as texture-region]))

(defn texture-region
  [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [texture (get textures file)]
    (if-let [[x y w h] bounds]
      (texture-region/f texture x y w h)
      (texture-region/f texture))))
