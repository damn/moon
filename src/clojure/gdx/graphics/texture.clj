(ns clojure.gdx.graphics.texture
  (:require clojure.graphics.texture)
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn create [^String path]
  (Texture. path))

(extend-type Texture
  clojure.graphics.texture/Texture
  (region
    ([texture x y width height]
     (TextureRegion. texture (int x) (int y) (int width) (int height)))
    ([texture]
     (TextureRegion. texture))))
