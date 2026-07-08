(ns clojure.batch
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d Batch TextureRegion)))

(def vertex-indices
  [[Batch/X1 Batch/Y1 Batch/C1 Batch/U1 Batch/V1]
   [Batch/X2 Batch/Y2 Batch/C2 Batch/U2 Batch/V2]
   [Batch/X3 Batch/Y3 Batch/C3 Batch/U3 Batch/V3]
   [Batch/X4 Batch/Y4 Batch/C4 Batch/U4 Batch/V4]])

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
