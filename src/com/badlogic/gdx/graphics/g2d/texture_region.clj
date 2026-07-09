(ns com.badlogic.gdx.graphics.g2d.texture-region
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn new
  ([texture x y w h]
   (TextureRegion. ^Texture texture (int x) (int y) (int w) (int h)))
  ([texture]
   (TextureRegion. ^Texture texture)))

(defn getRegionHeight [region]
  (.getRegionHeight ^TextureRegion region))

(defn getRegionWidth [region]
  (.getRegionWidth ^TextureRegion region))

(defn getTexture [region]
  (.getTexture ^TextureRegion region))

(defn getU [region]
  (.getU ^TextureRegion region))

(defn getU2 [region]
  (.getU2 ^TextureRegion region))

(defn getV [region]
  (.getV ^TextureRegion region))

(defn getV2 [region]
  (.getV2 ^TextureRegion region))
