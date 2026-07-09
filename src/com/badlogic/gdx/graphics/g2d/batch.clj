(ns com.badlogic.gdx.graphics.g2d.batch
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d Batch TextureRegion)))

(def X1 Batch/X1)
(def Y1 Batch/Y1)
(def C1 Batch/C1)
(def U1 Batch/U1)
(def V1 Batch/V1)
(def X2 Batch/X2)
(def Y2 Batch/Y2)
(def C2 Batch/C2)
(def U2 Batch/U2)
(def V2 Batch/V2)
(def X3 Batch/X3)
(def Y3 Batch/Y3)
(def C3 Batch/C3)
(def U3 Batch/U3)
(def V3 Batch/V3)
(def X4 Batch/X4)
(def Y4 Batch/Y4)
(def C4 Batch/C4)
(def U4 Batch/U4)
(def V4 Batch/V4)

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
