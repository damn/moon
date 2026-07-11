(ns moon.orthographic-camera
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]))

(defn set-zoom! [camera amount]
  (orthographic-camera/set-zoom! camera amount)
  (orthographic-camera/update camera))

(defn inc-zoom! [camera by]
  (set-zoom! camera (max 0.1 (+ (orthographic-camera/zoom camera) by))))
