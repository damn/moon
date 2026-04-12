(ns clj.api.com.badlogic.gdx.graphics.texture
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn region
  ([^Texture texture x y width height]
   (TextureRegion. texture (int x) (int y) (int width) (int height)))
  ([^Texture texture]
   (TextureRegion. texture)))

(defn create [^String path]
  (Texture. path))
