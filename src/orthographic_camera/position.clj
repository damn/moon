(ns orthographic-camera.position
  (:require [clojure.gdx.orthographic-camera.position :as position]
            [com.badlogic.gdx.math.vector3 :as vector3]))

(defn f [camera]
  (vector3/clojurize (position/f camera)))
