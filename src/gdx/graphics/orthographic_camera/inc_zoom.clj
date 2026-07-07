(ns gdx.graphics.orthographic-camera.inc-zoom
  (:require [clojure.orthographic-camera :as orthographic-camera]
            [gdx.graphics.orthographic-camera.set-zoom :refer [set-zoom!]]))

(defn inc-zoom! [cam by]
  (set-zoom! cam (max 0.1 (+ (orthographic-camera/zoom cam) by))))
