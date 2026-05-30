(ns com.badlogic.gdx.graphics.g2d.batch
  (:import (com.badlogic.gdx.graphics.g2d Batch
                                          TextureRegion)))

(defn draw!
  ([^Batch batch texture-region a b c d e f g h i]
   (.draw batch
          texture-region
          a
          b
          c
          d
          e
          f
          g
          h
          i))
  ([^Batch batch ^TextureRegion texture-region x y w h]
   (.draw batch texture-region (float x) (float y) (float w) (float h))))
