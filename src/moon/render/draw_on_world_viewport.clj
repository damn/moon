(ns moon.render.draw-on-world-viewport
  (:require [moon.draws :as draws])
  (:import (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.utils.viewport Viewport)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn do!
  [{:keys [^Batch ctx/batch
           ^ShapeDrawer ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}
   draw-fns]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with vis ui tooltip! it changes batch color somehow and does not
  ; change it back !
  (.setColor batch 1 1 1 1)
  (.setProjectionMatrix batch (.combined (Viewport/.getCamera world-viewport)))
  (.begin batch)
  (let [old-line-width (.getDefaultLineWidth shape-drawer)]
    (.setDefaultLineWidth shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [f draw-fns]
      (draws/handle! ctx (f ctx)))
    (reset! unit-scale 1)
    (.setDefaultLineWidth shape-drawer old-line-width))
  (.end batch)
  ctx)
