(ns moon.render.draw-on-world-viewport
  (:require [clj.api.space.earlygrey.shape-drawer :as sd]
            [moon.ctx :as ctx]
            [moon.graphics.camera :as camera])
  (:import (com.badlogic.gdx.graphics.g2d Batch)
           (com.badlogic.gdx.utils.viewport Viewport)))

(defn do!
  [{:keys [^Batch ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}
   draw-fns]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with vis ui tooltip! it changes batch color somehow and does not
  ; change it back !
  (.setColor batch 1 1 1 1)
  (.setProjectionMatrix batch (camera/combined (Viewport/.getCamera world-viewport)))
  (.begin batch)
  (sd/with-line-width shape-drawer world-unit-scale
    (reset! unit-scale world-unit-scale)
    (doseq [f draw-fns]
      (ctx/draw! ctx (f ctx)))
    (reset! unit-scale 1))
  (.end batch)
  ctx)
