(ns clojure.gdx.graphics.g2d.batch
  (:import (com.badlogic.gdx.graphics.g2d Batch
                                          TextureRegion)))

(defn draw!
  ([^Batch batch texture-region x y origin-x origin-y w h scale-x scale-y rotation]
   (.draw batch
          texture-region
          x
          y
          origin-x
          origin-y
          w
          h
          scale-x
          scale-y
          rotation))
  ([^Batch batch ^TextureRegion texture-region x y w h]
   (.draw batch
          texture-region
          (float x)
          (float y)
          (float w)
          (float h))))

(defn set-color! [^Batch batch [r g b a]]
  (.setColor batch r g b a))

(defn set-projection-matrix! [^Batch batch matrix]
  (.setProjectionMatrix batch matrix))

(defn begin! [^Batch batch]
  (.begin batch))

(defn end! [^Batch batch]
  (.end batch))
