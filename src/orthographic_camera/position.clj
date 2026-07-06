(ns orthographic-camera.position
  (:require [clojure.gdx.orthographic-camera.position :as position]
            [clojure.gdx.vector3.clojurize :as clojurize]))

(defn f [camera]
  (clojurize/f (position/f camera)))
