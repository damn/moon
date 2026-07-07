(ns clojure.draw-on-world-viewport
  (:require [clojure.begin :as begin]
            [clojure.draw :refer [draw!]]
            [clojure.end :as end]
            [clojure.graphics-shape-drawer :as shape-drawer]
            [clojure.orthographic-camera :as orthographic-camera]
            [clojure.set-color :as set-color]
            [clojure.set-projection-matrix :as set-projection-matrix]
            [clojure.world-unit-scale :as world-unit-scale]))

(defn step
  [{:keys [ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-viewport]
    :as ctx}
   draw-fns]
  ; fix scene2d.ui.tooltip flickering
  ; _everything_ flickers with TextToolTip!
  ; it changes batch color somehow and does not change it back ! FIXME
  (set-color/f batch 1 1 1 1)
  (set-projection-matrix/f batch (orthographic-camera/combined (:viewport/camera world-viewport)))
  (begin/f batch)
  (let [old-line-width (shape-drawer/get-default-line-width shape-drawer)]
    (shape-drawer/set-default-line-width! shape-drawer (* world-unit-scale/world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale/world-unit-scale)
    (doseq [[f & params] draw-fns]
      (draw! ctx (apply f ctx params)))
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (end/f batch)
  ctx)
