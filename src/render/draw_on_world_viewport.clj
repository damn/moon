(ns render.draw-on-world-viewport
  (:require
            [com.badlogic.gdx.graphics.g2d.batch :as batch]
            [clojure.gdx.orthographic-camera.combined :as combined]
            [clojure.gdx.shape-drawer.get-default-line-width :as get-default-line-width]
            [clojure.gdx.shape-drawer.set-default-line-width :as set-default-line-width]
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
  (batch/set-projection-matrix! batch (combined/f (:viewport/camera world-viewport)))
  (batch/begin! batch)
  (let [old-line-width (get-default-line-width/f shape-drawer)]
    (set-default-line-width/f shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [[f & params] draw-fns]
      (draw! ctx (apply f ctx params)))
    (reset! unit-scale 1)
    (set-default-line-width/f shape-drawer old-line-width))
  (batch/end! batch)
  ctx)
