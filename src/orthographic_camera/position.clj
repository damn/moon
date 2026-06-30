(ns orthographic-camera.position
  (:require [clojure.gdx :as gdx]))

(defn f [camera]
  (gdx/vector3-clojurize (gdx/camera-position camera)))
