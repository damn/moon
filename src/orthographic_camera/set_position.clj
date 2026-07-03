(ns orthographic-camera.set-position
  (:require [clojure.gdx.orthographic-camera.position :as position]
            [clojure.gdx.orthographic-camera.update :as update!]
            [clojure.gdx.vector3.set-x :as set-x]
            [clojure.gdx.vector3.set-y :as set-y]))

(defn set-position! [camera [x y]]
  (let [pos (position/f camera)]
    (set-x/f pos x)
    (set-y/f pos y))
  (update!/f! camera))
