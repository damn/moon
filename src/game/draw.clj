(ns game.draw
  (:require [clojure.string :as str])
  (:import (com.badlogic.gdx.graphics.g2d Batch BitmapFont BitmapFont$BitmapFontData TextureRegion)
           (com.badlogic.gdx.utils Align)
           (space.earlygrey.shapedrawer ShapeDrawer)))

(defn draw-fn-filled-rectangle [shape-drawer x y w h color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.filledRectangle ^ShapeDrawer shape-drawer (float x) (float y) (float w) (float h)))

(defn draw-fn-rectangle [shape-drawer x y w h color-float-bits]
  (.setColor ^ShapeDrawer shape-drawer (float color-float-bits))
  (.rectangle ^ShapeDrawer shape-drawer x y w h))

(defn draw-fn-text [batch default-font unit-scale {:keys [font scale x y text up?]}]
  (let [font (or font default-font)
        scale (or scale 1)
        font-data (.getData ^BitmapFont font)
        old-scale (.scaleX ^BitmapFont$BitmapFontData font-data)
        target-width 0
        wrap? false
        scale (* (float @unit-scale)
                 (float scale))]
    (.setScale ^BitmapFont$BitmapFontData font-data (* old-scale scale))
    (.draw ^BitmapFont font
           ^Batch batch
           text
           (float x)
           (float (+ y (if up?
                         (-> text
                             (str/split #"\n")
                             count
                             (* (.getLineHeight ^BitmapFont font)))
                         0)))
           (float target-width)
           Align/center
           wrap?)
    (.setScale ^BitmapFont$BitmapFontData font-data old-scale)))

(defn draw-fn-texture-region [batch unit-scale texture-region [x y] & {:keys [center? rotation]}]
  (let [[w h] (let [dimensions [(.getRegionWidth ^TextureRegion texture-region)
                                (.getRegionHeight ^TextureRegion texture-region)]]
                  (if (= @unit-scale 1)
                    dimensions
                    (mapv (comp float (partial * @unit-scale))
                          dimensions)))]
    (if center?
      (Batch/.draw ^Batch batch
                   ^TextureRegion texture-region
                   (float (- (float x) (/ (float w) 2)))
                   (float (- (float y) (/ (float h) 2)))
                   (float (/ (float w) 2))
                   (float (/ (float h) 2))
                   (float w)
                   (float h)
                   (float 1)
                   (float 1)
                   (float (or rotation 0)))
      (.draw ^Batch batch
             ^TextureRegion texture-region
             (float x)
             (float y)
             (float w)
             (float h)))))
