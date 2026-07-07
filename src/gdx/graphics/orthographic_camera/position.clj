(ns gdx.graphics.orthographic-camera.position
  (:require [clojure.orthographic-camera :as orthographic-camera]
            [clojure.vector3 :as vector3]))

(defn f [camera]
  (vector3/clojurize (orthographic-camera/position camera)))
