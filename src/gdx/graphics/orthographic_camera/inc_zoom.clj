(ns gdx.graphics.orthographic-camera.inc-zoom
  (:require [gdx.graphics.orthographic-camera.get-zoom :refer [get-zoom]]
            [gdx.graphics.orthographic-camera.set-zoom :refer [set-zoom!]]))

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (get-zoom cam) by))))
