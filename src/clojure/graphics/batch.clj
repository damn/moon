(ns clojure.graphics.batch
  (:import (com.badlogic.gdx.graphics.g2d SpriteBatch
                                          TextureRegion)))

(defprotocol Batch
  (draw! [_ texture-region x y origin-x origin-y width height scale-x scale-y rotation]
         [_ texture-region x y w h])
  (set-color! [_ r g b a])
  (set-projection-matrix! [_ matrix])
  (begin! [_])
  (end! [_]))

(extend-type SpriteBatch
  Batch
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
