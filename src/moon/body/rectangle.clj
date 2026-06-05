(ns moon.body.rectangle
  (:require [clojure.gdx.math.rectangle :refer [rectangle]]))

(defn ->rectangle
  [{:keys [body/position
           body/width
           body/height]}]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    (rectangle [x y width height])))
