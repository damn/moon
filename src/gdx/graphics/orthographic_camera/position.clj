(ns gdx.graphics.orthographic-camera.position
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.math.vector3 :as vector3]))

(defn f [camera]
  (vector3/clojurize (orthographic-camera/position camera)))
