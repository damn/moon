(ns levelgen-test.zoom-to-rect
  (:require [orthographic-camera.set-zoom :refer [set-zoom!]]
            [orthographic-camera.calculate-zoom :refer [calculate-zoom]]))

(defn f [camera rectangle]
  (set-zoom! camera
             (calculate-zoom camera
                             rectangle)))
