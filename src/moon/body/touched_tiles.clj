(ns moon.body.touched-tiles
  (:require [clojure.math.rectangle :as rectangle]))

(defn touched-tiles
  [{:keys [body/position
           body/width
           body/height]}]
  (rectangle/touched-tiles
   {:x (- (position 0) (/ width  2))
    :y (- (position 1) (/ height 2))
    :width  width
    :height height}))
