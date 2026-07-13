(ns clojure.gdx.graphics.g2d.texture-region
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]))

(defn new
  ([texture]
   (texture-region/new texture))
  ([texture x y w h]
   (texture-region/new texture x y w h)))

(defn get-region-width [texture-region]
  (texture-region/getRegionWidth texture-region))

(defn get-region-height [texture-region]
  (texture-region/getRegionHeight texture-region))
