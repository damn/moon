(ns texture.texture-region
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region]
            [com.badlogic.gdx.graphics.texture :as texture]))

(defn f
  ([texture]
   (texture-region/create texture))
  ([texture x y w h]
   (texture-region/create-sub texture x y w h)))
