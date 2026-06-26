(ns orthographic-camera.get-viewport-height
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as camera]))

(defn get-viewport-height [camera]
  (camera/viewport-height camera))
