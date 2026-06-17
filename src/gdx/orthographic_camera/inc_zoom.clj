(ns gdx.orthographic-camera.inc-zoom
  (:require [com.badlogic.gdx.graphics.orthographic-camera.get-zoom :refer [get-zoom]]
            [com.badlogic.gdx.graphics.orthographic-camera.set-zoom :refer [set-zoom!]]))

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (get-zoom cam) by))))
