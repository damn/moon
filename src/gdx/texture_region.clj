(ns gdx.texture-region
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn create
  ([texture]
   (TextureRegion. ^Texture texture))
  ([texture x y w h]
   (TextureRegion. ^Texture texture (int x) (int y) (int w) (int h))))

(defn get-region-width [texture-region]
  (.getRegionWidth ^TextureRegion texture-region))

(defn get-region-height [texture-region]
  (.getRegionHeight ^TextureRegion texture-region))

(defn get-u [texture-region]
  (.getU ^TextureRegion texture-region))

(defn get-v [texture-region]
  (.getV ^TextureRegion texture-region))

(defn get-u2 [texture-region]
  (.getU2 ^TextureRegion texture-region))

(defn get-v2 [texture-region]
  (.getV2 ^TextureRegion texture-region))

(defn get-texture [texture-region]
  (.getTexture ^TextureRegion texture-region))
