(ns clojure.orthographic-camera.zoom-to-rect
  (:require [clojure.set-zoom :refer [set-zoom!]]
            [clojure.calculate-zoom :refer [calculate-zoom]]))

(defn f [camera rectangle]
  (set-zoom! camera
             (calculate-zoom camera
                             rectangle)))
