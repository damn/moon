(ns clojure.gdx.graphics.g2d.texture-region
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]))

(defn create
  ([texture]
   (texture-region/new texture))
  ([texture x y w h]
   (texture-region/new texture x y w h)))

(defn get-region-width [texture-region]
  (texture-region/getRegionWidth texture-region))

(defn get-region-height [texture-region]
  (texture-region/getRegionHeight texture-region))

(defn get-u [texture-region]
  (texture-region/getU texture-region))

(defn get-v [texture-region]
  (texture-region/getV texture-region))

(defn get-u2 [texture-region]
  (texture-region/getU2 texture-region))

(defn get-v2 [texture-region]
  (texture-region/getV2 texture-region))

(defn get-texture [texture-region]
  (texture-region/getTexture texture-region))
