(ns orthographic-camera.set-position
  (:require [clojure.gdx :as gdx]))

(defn set-position! [camera [x y]]
  (gdx/camera-set-position! camera x y)
  (gdx/camera-update! camera))
