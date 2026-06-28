(ns batch.setup-drawing
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

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
