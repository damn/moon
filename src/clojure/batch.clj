(ns clojure.batch
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d Batch TextureRegion)))

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

(defn begin! [batch]
  (Batch/.begin batch))

(defn end! [batch]
  (Batch/.end batch))

(defn set-color! [^Batch batch r g b a]
  (Batch/.setColor batch (float r) (float g) (float b) (float a)))

(defn get-color [batch]
  (Batch/.getColor batch))

(defn set-projection-matrix! [batch matrix4]
  (Batch/.setProjectionMatrix batch matrix4))

(defn draw-vertices! [batch texture verts offset cnt]
  (Batch/.draw batch
               ^Texture texture
               ^floats verts
               (int offset)
               (int cnt)))

(defn draw-texture-region!
  ([^Batch batch ^TextureRegion texture-region x y w h]
   (Batch/.draw batch texture-region (float x) (float y) (float w) (float h)))
  ([^Batch batch ^TextureRegion texture-region x y origin-x origin-y w h scale-x scale-y rotation]
   (Batch/.draw batch
                texture-region
                (float x)
                (float y)
                (float origin-x)
                (float origin-y)
                (float w)
                (float h)
                (float scale-x)
                (float scale-y)
                (float rotation))))
