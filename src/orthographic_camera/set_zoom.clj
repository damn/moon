(ns orthographic-camera.set-zoom
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as camera]
            [orthographic-camera.update :refer [update!]]))

(defn set-zoom! [camera amount]
  (camera/set-zoom! camera amount)
  (update! camera))
