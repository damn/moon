(ns clojure.com.badlogic.gdx.graphics.g2d.texture-region
  "Defines a rectangular area of a texture. The coordinate system used has its origin in the upper left corner with the x-axis
  pointing to the right and the y axis pointing downwards."
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn create
  "Constructs a region the size of the specified texture."
  ([^Texture texture]
   (TextureRegion. texture))
  ([^Texture texture x y w h]
   (TextureRegion. texture (int x) (int y) (int w) (int h))))

(defn texture [^TextureRegion texture-region]
  (.getTexture texture-region))

(defn region-width
  "Returns the region's width."
  [^TextureRegion texture-region]
  (.getRegionWidth texture-region))

(defn region-height
  "Returns the region's height."
  [^TextureRegion texture-region]
  (.getRegionHeight texture-region))

(defn u [^TextureRegion texture-region]
  (.getU texture-region))

(defn v [^TextureRegion texture-region]
  (.getV texture-region))

(defn u2 [^TextureRegion texture-region]
  (.getU2 texture-region))

(defn v2 [^TextureRegion texture-region]
  (.getV2 texture-region))
