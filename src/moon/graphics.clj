(ns moon.graphics
  (:require [clojure.graphics.orthographic-camera :as camera]
            [clojure.gdx.utils.viewport :as viewport]
            [clojure.graphics.texture-region :as texture-region]
            [clojure.string :as str])
  (:import (com.badlogic.gdx.graphics.g2d Batch
                                          BitmapFont)
           (com.badlogic.gdx.utils Align)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn draw-text!
  [{:keys [ctx/batch
           ctx/unit-scale
           ctx/default-font]}
   {:keys [font scale x y text h-align up?]}]
  (let [^BitmapFont font (or font default-font)
        old-scale (.scaleX (.getData font))
        target-width 0
        wrap? false
        scale (* (float @unit-scale)
                 (float (or scale 1)))]
    (.setScale (.getData font) (* old-scale scale))
    (.draw font
           batch
           text
           (float x)
           (float (+ y (if up?
                         (-> text
                             (str/split #"\n")
                             count
                             (* (.getLineHeight font)))
                         0)))
           (float target-width)
           Align/center
           wrap?)
    (.setScale (.getData font) old-scale)))

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
           ^ShapeDrawer ctx/shape-drawer
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
  (let [old-line-width (.getDefaultLineWidth shape-drawer)]
    (.setDefaultLineWidth shape-drawer (* world-unit-scale old-line-width))
    (reset! unit-scale world-unit-scale)
    (f)
    (reset! unit-scale 1)
    (.setDefaultLineWidth shape-drawer old-line-width))
  (.end batch))
