(ns orthographic-camera.get-viewport-width
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as camera]))

(defn get-viewport-width [camera]
  (camera/viewport-width camera))
