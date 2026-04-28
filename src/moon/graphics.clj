(ns moon.graphics
  (:require [clojure.graphics.bitmap-font :as bitmap-font]
            [clojure.graphics.orthographic-camera :as camera]
            [clojure.graphics.viewport :as viewport]
            [clojure.graphics.texture-region :as texture-region]
            [clojure.graphics.shape-drawer :as shape-drawer])
  (:import (com.badlogic.gdx.graphics.g2d Batch)))

(defn draw-text!
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/default-font]}
   {:keys [font scale x y text h-align up?]}]
  (let [font (or font default-font)
        old-scale (bitmap-font/scale-x font)
        target-width 0
        wrap? false
        scale (* (float @unit-scale)
                 (float (or scale 1)))]
    (bitmap-font/set-scale! font (* old-scale scale))
    (bitmap-font/draw! font
                       batch
                       text
                       x
                       (+ y (if up? (bitmap-font/text-height font text) 0))
                       target-width
                       (or h-align :align/center)
                       wrap?)
    (bitmap-font/set-scale! font old-scale)))

(defn draw-texture-region!
  [{:keys [^Batch ctx/batch
           ctx/unit-scale
           ctx/world-unit-scale]}
   texture-region
   [x y]
   & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(texture-region/width  texture-region)
                                (texture-region/height texture-region)]]
                (if (= @unit-scale 1)
                  dimensions
                  (mapv (comp float (partial * world-unit-scale))
                        dimensions)))]
    (if center?
      (.draw batch
             texture-region
             (- (float x) (/ (float w) 2))
             (- (float y) (/ (float h) 2))
             (/ (float w) 2)
             (/ (float h) 2)
             w
             h
             1
             1
             (or rotation 0))
      (.draw batch texture-region (float x) (float y) (float w) (float h)))))

(defn draw-on-world-viewport!
  [{:keys [^Batch ctx/batch
           ctx/shape-drawer
           ctx/unit-scale
           ctx/world-unit-scale
           ctx/world-viewport]}
   f]
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
    (f)
    (reset! unit-scale 1)
    (shape-drawer/set-default-line-width! shape-drawer old-line-width))
  (.end batch))
