(ns moon.render.draw-on-world-viewport
  (:require [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.viewport :as viewport]
            [clojure.graphics.shape-drawer :as shape-drawer]
            [moon.draws :as draws])
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

(defn step
  [{:keys [^Batch ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]
    :as ctx}
   draw-fns]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with TextToolTip!
  ; it changes batch color somehow and does not change it back ! FIXME
  (.setColor batch 1 1 1 1)
  ;
  (.setProjectionMatrix batch (camera/combined (viewport/camera world-viewport)))
  (.begin batch)
  (let [old-line-width (shape-drawer/default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (doseq [f draw-fns]
      (draws/handle ctx (f ctx)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (.end batch)
  ctx)
