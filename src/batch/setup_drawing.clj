(ns batch.setup-drawing
  (:require [com.badlogic.gdx.graphics.g2d.batch :as batch]))

(defn setup-drawing! [batch projection-matrix f]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with TextToolTip!
  ; it changes batch color somehow and does not change it back ! FIXME
  (batch/set-color! batch 1 1 1 1)
  ;
  (batch/set-projection-matrix! batch projection-matrix)
  (batch/begin! batch)
  (f)
  (batch/end! batch))
