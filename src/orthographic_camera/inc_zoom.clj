(ns orthographic-camera.inc-zoom
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [orthographic-camera.set-zoom :refer [set-zoom!]]))

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (orthographic-camera/zoom cam) by))))
