(ns orthographic-camera.inc-zoom
  (:require [clojure.gdx.orthographic-camera.zoom :as zoom]
            [orthographic-camera.set-zoom :refer [set-zoom!]]))

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (zoom/f cam) by))))
