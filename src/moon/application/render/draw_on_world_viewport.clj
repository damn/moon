(ns moon.application.render.draw-on-world-viewport
  (:require [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [clojure.gdx.utils.viewport :as viewport]
            [clojure.graphics.orthographic-camera :as camera]
            [moon.draws :as draws]
            [space.earlygrey.shapedrawer.shape-drawer :as shape-drawer]))

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
  (batch/set-color! batch 1 1 1 1)
  ;
  (batch/set-projection-matrix! batch (camera/combined (viewport/camera world-viewport)))
  (batch/begin! batch)
  (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [f draw-fns]
      (draws/handle ctx (f ctx)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (batch/end! batch)
  ctx)
