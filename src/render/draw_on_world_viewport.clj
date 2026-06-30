(ns render.draw-on-world-viewport
  (:require [clojure.gdx :as gdx]
            [ctx.draw :refer [draw!]])
  (:import (space.earlygrey.shapedrawer ShapeDrawer)))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}
   draw-fns]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with TextToolTip!
  ; it changes batch color somehow and does not change it back ! FIXME
  (gdx/batch-set-color! batch 1 1 1 1)
  (gdx/batch-set-projection-matrix! batch (gdx/camera-combined (:viewport/camera world-viewport)))
  (gdx/batch-begin! batch)
  (let [old-line-width (ShapeDrawer/.getDefaultLineWidth shape-drawer)]
    (ShapeDrawer/.setDefaultLineWidth shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [[f & params] draw-fns]
      (draw! ctx (apply f ctx params)))
    (reset! unit-scale 1)
    (ShapeDrawer/.setDefaultLineWidth shape-drawer old-line-width))
  (gdx/batch-end! batch)
  ctx)
