(ns com.badlogic.gdx.graphics.g2d.batch
  (:import (com.badlogic.gdx.graphics Texture)
           (com.badlogic.gdx.graphics.g2d Batch TextureRegion)))

(defn begin! [batch]
  (Batch/.begin batch))

(defn draw! [batch texture verts offset cnt]
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

(defn end! [batch]
  (Batch/.end batch))

(defn get-color [batch]
  (Batch/.getColor batch))

(defn set-color! [^Batch batch r g b a]
  (Batch/.setColor batch (float r) (float g) (float b) (float a)))

(defn set-projection-matrix! [batch matrix4]
  (Batch/.setProjectionMatrix batch matrix4))
