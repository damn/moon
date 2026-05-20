(ns com.badlogic.gdx.graphics.g2d.sprite-batch
  (:require [gdl.app :as app]
            [gdl.graphics.batch :as batch])
  (:import (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)))

(.bindRoot #'app/sprite-batch
           (fn []
             (SpriteBatch.)))

(extend-type SpriteBatch
  batch/Batch
  (begin! [batch]
    (.begin batch))

  (end! [batch]
    (.end batch))

  (set-color! [batch r g b a]
    (.setColor batch r g b a))

  (set-projection-matrix! [batch matrix]
    (.setProjectionMatrix batch matrix))

  (draw!
    ([batch texture-region x y origin-x origin-y width height scale-x scale-y rotation]
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
    ([batch texture-region x y w h]
     (.draw batch ^TextureRegion texture-region (float x) (float y) (float w) (float h)))))
