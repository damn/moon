(ns orthographic-camera.set-position
  (:require [clojure.gdx.orthographic-camera.position :as position]
            [clojure.gdx.orthographic-camera.update :as update!]))

(defn set-position! [camera [x y]]
  (let [pos (position/f camera)]
    (set! (.x pos) x)
    (set! (.y pos) y))
  (update!/f! camera))
