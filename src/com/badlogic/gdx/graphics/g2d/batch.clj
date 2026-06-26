(ns com.badlogic.gdx.graphics.g2d.batch
  (:require [com.badlogic.gdx.graphics.texture :as texture]
            [com.badlogic.gdx.graphics.g2d.texture-region :as texture-region])
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

(def x1 Batch/X1)
(def y1 Batch/Y1)
(def c1 Batch/C1)
(def u1 Batch/U1)
(def v1 Batch/V1)
(def x2 Batch/X2)
(def y2 Batch/Y2)
(def c2 Batch/C2)
(def u2 Batch/U2)
(def v2 Batch/V2)
(def x3 Batch/X3)
(def y3 Batch/Y3)
(def c3 Batch/C3)
(def u3 Batch/U3)
(def v3 Batch/V3)
(def x4 Batch/X4)
(def y4 Batch/Y4)
(def c4 Batch/C4)
(def u4 Batch/U4)
(def v4 Batch/V4)

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
          (float h)))
  ([^Batch batch texture verts a b]
   (.draw batch
          (texture/type-hint texture)
          ^floats verts
          (int a)
          (int b))))

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
