(ns clojure.texture-region
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn get-region-height [region]
  (TextureRegion/.getRegionHeight region))

(defn get-region-width [region]
  (TextureRegion/.getRegionWidth region))

(defn get-texture [region]
  (TextureRegion/.getTexture region))

(defn get-u [region]
  (TextureRegion/.getU region))

(defn get-u2 [region]
  (TextureRegion/.getU2 region))

(defn get-v [region]
  (TextureRegion/.getV region))

(defn get-v2 [region]
  (TextureRegion/.getV2 region))

(defn new
  ([texture x y w h]
   (TextureRegion. ^Texture texture (int x) (int y) (int w) (int h)))
  ([texture]
   (TextureRegion. ^Texture texture)))
