(ns gdx.graphics.orthographic-camera
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as camera]))

(defn create
  [{:keys [y-down?
           world-width
           world-height]}]
  (doto (camera/create)
    (camera/set-to-ortho! y-down? world-width world-height)))
