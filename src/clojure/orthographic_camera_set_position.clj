(ns clojure.orthographic-camera-set-position
  (:require [com.badlogic.gdx.graphics.orthographic-camera :as orthographic-camera]
            [com.badlogic.gdx.math.vector3 :as gdx-vector3]))

(defn set-position! [camera [x y]]
  (let [pos (orthographic-camera/position camera)]
    (gdx-vector3/set-x! pos x)
    (gdx-vector3/set-y! pos y))
  (orthographic-camera/update camera))
