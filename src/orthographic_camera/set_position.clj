(ns orthographic-camera.set-position
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as camera]
            [orthographic-camera.update :refer [update!]]))

(defn set-position! [camera [x y]]
  (set! (.x (camera/position camera)) x)
  (set! (.y (camera/position camera)) y)
  (update! camera))
