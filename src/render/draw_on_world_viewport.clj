(ns render.draw-on-world-viewport
  (:require [clojure.gdx.orthographic-camera.combined :as combined]
            [ctx.draw :refer [draw!]])
  (:import (com.badlogic.gdx.graphics.g2d Batch)
           (space.earlygrey.shapedrawer ShapeDrawer)))

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
  (Batch/.setColor batch 1 1 1 1)
  (Batch/.setProjectionMatrix batch (combined/f (:viewport/camera world-viewport)))
  (Batch/.begin batch)
  (let [old-line-width (ShapeDrawer/.getDefaultLineWidth shape-drawer)]
    (ShapeDrawer/.setDefaultLineWidth shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [[f & params] draw-fns]
      (draw! ctx (apply f ctx params)))
    (reset! unit-scale 1)
    (ShapeDrawer/.setDefaultLineWidth shape-drawer old-line-width))
  (Batch/.end batch)
  ctx)
