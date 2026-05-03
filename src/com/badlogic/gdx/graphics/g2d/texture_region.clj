(ns com.badlogic.gdx.graphics.g2d.texture-region
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn width [^TextureRegion texture-region]
  (.getRegionWidth texture-region))

(defn height [^TextureRegion texture-region]
  (.getRegionHeight texture-region))

(defn create
  ([^Texture texture]
   (TextureRegion. texture))
  ([^Texture texture x y width height]
   (TextureRegion. ^Texture texture
                   (int x)
                   (int y)
                   (int width)
                   (int height))))
