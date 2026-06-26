(ns com.badlogic.gdx.graphics.g2d.texture-region
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn type-hint
  ^TextureRegion
  [obj]
  obj)

(defn width [^TextureRegion obj]
  (.getRegionWidth obj))

(defn height [^TextureRegion obj]
  (.getRegionHeight obj))

(defn u [^TextureRegion obj]
  (.getU obj))

(defn u2 [^TextureRegion obj]
  (.getU2 obj))

(defn v [^TextureRegion obj]
  (.getV obj))

(defn v2 [^TextureRegion obj]
  (.getV2 obj))

(defn texture [^TextureRegion obj]
  (.getTexture obj))
