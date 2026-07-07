(ns clojure.body-rectangle
  (:require [clojure.rectangle :as rectangle]))

(defn ->rectangle
  [{:keys [body/position
           body/width
           body/height]}]
  (let [[x y] [(- (position 0) (/ width  2))
               (- (position 1) (/ height 2))]]
    (rectangle/new x y width height)))
