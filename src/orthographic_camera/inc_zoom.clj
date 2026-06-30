(ns orthographic-camera.inc-zoom
  (:require [clojure.gdx :as gdx]
            [orthographic-camera.set-zoom :refer [set-zoom!]]))

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (gdx/camera-zoom cam) by))))
