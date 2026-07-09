(ns clojure.orthographic-camera.visible-tiles
  (:require [clojure.orthographic-camera-frustum :refer [frustum]]))

(defn visible-tiles [camera]
  (let [[left-x right-x bottom-y top-y] (frustum camera)]
    (for [x (range (int left-x)   (int right-x))
          y (range (int bottom-y) (+ 2 (int top-y)))]
      [x y])))
