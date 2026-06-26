(ns com.badlogic.gdx.graphics.g2d.batch
  (:require [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region])
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

(defn draw!
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
   (.draw batch
          (texture-region/type-hint texture-region)
          (float x)
          (float y)
          (float w)
          (float h))))

(defn set-projection-matrix! [^Batch batch matrix4]
  (.setProjectionMatrix batch matrix4))

(defn begin! [^Batch batch]
  (.begin batch))

(defn end! [^Batch batch]
  (.end batch))

(defn color [^Batch batch]
  (.getColor batch))

(defn set-color! [^Batch batch r g b a]
  (.setColor batch r g b a))
