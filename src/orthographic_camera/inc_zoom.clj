(ns orthographic-camera.inc-zoom
  (:require [clojure.orthographic-camera.get-zoom :refer [get-zoom]]
            [clojure.orthographic-camera.set-zoom :refer [set-zoom!]]))

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (get-zoom cam) by))))
