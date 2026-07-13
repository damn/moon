(ns gdx.batch
  (:require [com.badlogic.gdx.graphics.g2d.batch :as batch]))

(def x1 batch/X1)
(def y1 batch/Y1)
(def c1 batch/C1)
(def u1 batch/U1)
(def v1 batch/V1)
(def x2 batch/X2)
(def y2 batch/Y2)
(def c2 batch/C2)
(def u2 batch/U2)
(def v2 batch/V2)
(def x3 batch/X3)
(def y3 batch/Y3)
(def c3 batch/C3)
(def u3 batch/U3)
(def v3 batch/V3)
(def x4 batch/X4)
(def y4 batch/Y4)
(def c4 batch/C4)
(def u4 batch/U4)
(def v4 batch/V4)

(defn get-color [batch]
  (batch/getColor batch))

(defn set-color! [batch r g b a]
  (batch/setColor batch r g b a))

(defn set-projection-matrix! [batch matrix]
  (batch/setProjectionMatrix batch matrix))

(defn begin! [batch]
  (batch/begin batch))

(defn end! [batch]
  (batch/end batch))

(defn draw! [batch & args]
  (apply batch/draw batch args))
