(ns com.badlogic.gdx.graphics.g2d.sprite-batch
  (:import (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)))

(defn create []
  (SpriteBatch.))

(defn begin! [^SpriteBatch batch]
  (.begin batch))

(defn end! [^SpriteBatch batch]
  (.end batch))

(defn set-color! [^SpriteBatch batch r g b a]
  (.setColor batch r g b a))

(defn set-projection-matrix! [^SpriteBatch batch matrix]
  (.setProjectionMatrix batch matrix))

(defn draw!
  ([^SpriteBatch batch texture-region x y origin-x origin-y width height scale-x scale-y rotation]
   (.draw batch
          ^TextureRegion texture-region
          x
          y
          origin-x
          origin-y
          width
          height
          scale-x
          scale-y
          rotation))
  ([^SpriteBatch batch texture-region x y w h]
   (.draw batch ^TextureRegion texture-region (float x) (float y) (float w) (float h))))
