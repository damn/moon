(ns orthographic-camera.set-zoom
  (:require
            [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]))

(defn set-zoom! [camera amount]
  (orthographic-camera/set-zoom! camera amount)
  (orthographic-camera/update! camera))
