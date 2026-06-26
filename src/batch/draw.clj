(ns batch.draw
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region])
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

(defn f!
  ([^Batch batch texture-region x y origin-x origin-y width height scale-x scale-y rotation]
   (.draw batch
          texture-region
          x
          y
          origin-x
          origin-y
          width
          height
          scale-x
          scale-y
          rotation))
  ([^Batch batch texture-region x y w h]
   (.draw batch (texture-region/type-hint texture-region) (float x) (float y) (float w) (float h))))
