(ns orthographic-camera.set-position
  (:require [clojure.gdx.orthographic-camera.position :as position]
            [clojure.gdx.orthographic-camera.update :as update!]
            [com.badlogic.gdx.math.vector3 :as vector3]))

(defn set-position! [camera [x y]]
  (let [pos (position/f camera)]
    (vector3/set-x! pos x)
    (vector3/set-y! pos y))
  (update!/f! camera))
