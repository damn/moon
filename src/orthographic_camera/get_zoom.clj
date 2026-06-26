(ns orthographic-camera.get-zoom
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as camera]))

(defn get-zoom [camera]
  (camera/zoom camera))

