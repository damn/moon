(ns render.draw-on-world-viewport
  (:require [clojure.gdx.batch.begin! :as begin!]
            [clojure.gdx.batch.end! :as end!]
            [clojure.gdx.batch.set-color! :as set-color!]
            [clojure.gdx.batch.set-projection-matrix! :as set-projection-matrix!]
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
  (set-color!/f batch 1 1 1 1)
  (set-projection-matrix!/f batch (combined/f (:viewport/camera world-viewport)))
  (begin!/f batch)
  (let [old-line-width (get-default-line-width/f shape-drawer)]
    (set-default-line-width/f shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [[f & params] draw-fns]
      (draw! ctx (apply f ctx params)))
    (reset! unit-scale 1)
    (set-default-line-width/f shape-drawer old-line-width))
  (end!/f batch)
  ctx)
