(ns ctx.render.draw-on-world-viewport
  (:require [gdx.graphics.shape-drawer :as shape-drawer]
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [ctx.draw :refer [draw!]]))

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
  (batch/set-projection-matrix! batch (orthographic-camera/combined (:viewport/camera world-viewport)))
  (batch/begin! batch)
  (let [old-line-width (shape-drawer/get-default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [[f & params] draw-fns]
      (draw! ctx (apply f ctx params)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (batch/end! batch)
  ctx)
