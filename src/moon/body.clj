(ns moon.body
  (:require [clojure.math.vector2 :as v]
            [moon.rectangle :as rectangle])
  (:import (com.badlogic.gdx.math Intersector
                                  Rectangle)))

(defn rectangle
  [{:keys [body/position
           body/width
           body/height]}]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    (Rectangle. x y width height)))

(defn touched-tiles
  [{:keys [body/position
           body/width
           body/height]}]
  (rectangle/touched-tiles
   {:x (- (position 0) (/ width  2))
    :y (- (position 1) (/ height 2))
    :width  width
    :height height}))

(defn overlaps? [body other-body]
  (Intersector/overlaps ^Rectangle (rectangle body)
                        ^Rectangle (rectangle other-body)))

(defn distance [body other-body]
  (v/distance (:body/position body)
              (:body/position other-body)))

(defn direction [body other-body]
  (v/direction (:body/position body)
               (:body/position other-body)))
