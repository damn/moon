(ns gdl.orthographic-camera.inc-zoom
  (:require [gdl.orthographic-camera.get-zoom :refer [get-zoom]]
            [gdl.orthographic-camera.set-zoom :refer [set-zoom!]]))

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (get-zoom cam) by))))
