(ns com.badlogic.gdx.graphics.g2d.texture-region
  (:require [com.badlogic.gdx.graphics.texture :as texture])
  (:import (com.badlogic.gdx.graphics.g2d TextureRegion)))

(defn create [texture]
  (TextureRegion. (texture/type-hint texture)))

(defn create-sub [texture x y w h]
  (TextureRegion. (texture/type-hint texture) (int x) (int y) (int w) (int h)))

(def java-class TextureRegion)

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
