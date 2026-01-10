(ns moon.math.geom
  (:refer-clojure :exclude [contains?])
  (:import (com.badlogic.gdx.math Circle
                                  Intersector
                                  Rectangle)))

(defn circle [x y radius]
  (Circle. x y radius))

(defn rectangle [x y width height]
  (Rectangle. x y width height))

(defmulti overlaps?
  (fn [a b] [(class a) (class b)]))

(defmethod overlaps? [Circle Circle]
  [^Circle a ^Circle b]
  (Intersector/overlaps a b))

(defmethod overlaps? [Rectangle Rectangle]
  [^Rectangle a ^Rectangle b]
  (Intersector/overlaps a b))

(defmethod overlaps? [Rectangle Circle]
  [^Rectangle rect ^Circle circle]
  (Intersector/overlaps circle rect))

(defmethod overlaps? [Circle Rectangle]
  [^Circle circle ^Rectangle rect]
  (Intersector/overlaps circle rect))

(defn contains?
  [rectangle [x y]]
  (Rectangle/.contains rectangle x y))

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
