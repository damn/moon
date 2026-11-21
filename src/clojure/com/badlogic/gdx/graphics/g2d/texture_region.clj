(ns clojure.com.badlogic.gdx.graphics.g2d.texture-region
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn create
  ([^Texture texture]
   (TextureRegion. texture))
  ([^Texture texture x y w h]
   (TextureRegion. texture (int x) (int y) (int w) (int h))))

(defn texture [^TextureRegion texture-region]
  (.getTexture texture-region))

(defn region-width [^TextureRegion texture-region]
  (.getRegionWidth texture-region))

(defn region-height [^TextureRegion texture-region]
  (.getRegionHeight texture-region))

(defn u [^TextureRegion texture-region]
  (.getU texture-region))

(defn v [^TextureRegion texture-region]
  (.getV texture-region))

(defn u2 [^TextureRegion texture-region]
  (.getU2 texture-region))

(defn v2 [^TextureRegion texture-region]
  (.getV2 texture-region))
