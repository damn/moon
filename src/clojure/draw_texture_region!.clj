(ns clojure.draw-texture-region!
  (:import (com.badlogic.gdx.graphics.g2d Batch TextureRegion)))

(defn f
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
