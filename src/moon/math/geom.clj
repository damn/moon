(ns moon.math.geom
  (:refer-clojure :exclude [contains?])
  (:require [gdl.math.circle :as circle]
            [gdl.math.intersector :as intersector]
            [gdl.math.rectangle :as rectangle]))

(def circle    circle/create)
(def rectangle rectangle/create)
(def overlaps? intersector/overlaps?)
(def contains? rectangle/contains?)

(defn circle->outer-rectangle [{[x y] :position :keys [radius]}]
  (let [radius (float radius)
        size (* radius 2)]
    {:x (- (float x) radius)
     :y (- (float y) radius)
     :width  size
     :height size}))

(defn body->gdx-rectangle [{:keys [body/position body/width body/height]}]
  {:pre [position width height]}
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    (rectangle x y width height)))

(defn rectangle->touched-tiles
  "x is leftmost point and y bottom most point in the rectangle."
  [{:keys [x y width height]}]
  {:pre [x y width height]}
  (let [x       (float x)
        y       (float y)
        width   (float width)
        height  (float height)
        l (int x)
        b (int y)
        r (int (+ x width))
        t (int (+ y height))]
    (set
     (if (or (> width 1) (> height 1))
       (for [x (range l (inc r))
             y (range b (inc t))]
         [x y])
       [[l b] [l t] [r b] [r t]]))))

(defn body->touched-tiles
  [{:keys [body/position body/width body/height]}]
  (rectangle->touched-tiles
   {:x (- (position 0) (/ width  2))
    :y (- (position 1) (/ height 2))
    :width  width
    :height height}))
