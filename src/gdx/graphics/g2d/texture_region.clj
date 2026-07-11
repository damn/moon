(ns gdx.graphics.g2d.texture-region
  (:refer-clojure :exclude [new])
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]))

(defn new
  ([texture]
   (texture-region/new texture))
  ([texture x y w h]
   (texture-region/new texture x y w h)))
