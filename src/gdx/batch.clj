(ns gdx.batch
  (:import (com.badlogic.gdx.graphics.g2d Batch
                                          TextureRegion)))

(defn draw!
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

(defn setup-drawing! [^Batch batch projection-matrix f]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with TextToolTip!
  ; it changes batch color somehow and does not change it back ! FIXME
  (.setColor batch 1 1 1 1)
  ;
  (.setProjectionMatrix batch projection-matrix)
  (.begin batch)
  (f)
  (.end batch))
