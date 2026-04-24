(ns clojure.gdx.graphics.g2d.sprite-batch
  (:require clojure.graphics.batch)
  (:import (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)))

(defn create []
  (SpriteBatch.))

(extend-type SpriteBatch
  clojure.graphics.batch/Batch
  (draw!
    ([batch texture-region x y origin-x origin-y width height scale-x scale-y rotation]
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
    ([batch ^TextureRegion texture-region x y w h]
     (.draw batch
            texture-region
            (float x)
            (float y)
            (float w)
            (float h))))

  (set-color! [batch r g b a]
    (.setColor batch r g b a))

  (set-projection-matrix! [batch matrix]
    (.setProjectionMatrix batch matrix))

  (begin! [batch]
    (.begin batch))

  (end! [batch]
    (.end batch)))
