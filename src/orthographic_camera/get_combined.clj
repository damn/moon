(ns orthographic-camera.get-combined
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as camera]))

(defn get-combined [camera]
  (camera/combined camera))
