(ns orthographic-camera.inc-zoom
  (:require [orthographic-camera.get-zoom :refer [get-zoom]]
            [orthographic-camera.set-zoom :refer [set-zoom!]]))

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (get-zoom cam) by))))
