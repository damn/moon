(ns clojure.orthographic-camera-position
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [gdl.math.vector3 :as vector3]))

(defn f [camera]
  (vector3/clojurize (orthographic-camera/position camera)))
