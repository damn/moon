(ns clojure.orthographic-camera-position
  (:require [gdl.orthographic-camera :as orthographic-camera]
            [gdl.vector3 :as vector3]))

(defn f [camera]
  (vector3/clojurize (orthographic-camera/position camera)))
