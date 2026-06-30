(ns moon.textures
  (:require [clojure.gdx :as gdx]))

(defn texture-region
  [textures {:keys [image/file image/bounds]}]
  (assert file)
  (assert (contains? textures file))
  (let [texture (get textures file)]
    (if-let [[x y w h] bounds]
      (gdx/texture-region texture (int x) (int y) (int w) (int h))
      (gdx/texture-region texture))))
