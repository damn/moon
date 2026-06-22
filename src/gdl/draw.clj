(ns gdl.draw
  (:import (com.badlogic.gdx.graphics.g2d Batch
                                          TextureRegion)))

(defn f!
  ([^Batch batch texture-region x y origin-x origin-y width height scale-x scale-y rotation]
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
  ([^Batch batch ^TextureRegion texture-region x y w h]
   (.draw batch texture-region (float x) (float y) (float w) (float h))))
