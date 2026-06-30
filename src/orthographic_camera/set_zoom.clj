(ns orthographic-camera.set-zoom
  (:require [clojure.gdx :as gdx]))

(defn set-zoom! [camera amount]
  (gdx/camera-set-zoom! camera amount)
  (gdx/camera-update! camera))
