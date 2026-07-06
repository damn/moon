(ns orthographic-camera.set-position
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.math.vector3 :as vector3]))

(defn set-position! [camera [x y]]
  (let [pos (orthographic-camera/position camera)]
    (vector3/set-x! pos x)
    (vector3/set-y! pos y))
  (orthographic-camera/update! camera))
