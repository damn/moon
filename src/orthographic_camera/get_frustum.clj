(ns orthographic-camera.get-frustum
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as camera]))

(defn get-frustum [camera]
  (camera/frustum camera))
