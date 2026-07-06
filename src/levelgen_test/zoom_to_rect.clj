(ns levelgen-test.zoom-to-rect
  (:require [gdx.graphics.orthographic-camera.set-zoom :refer [set-zoom!]]
            [gdx.graphics.orthographic-camera.calculate-zoom :refer [calculate-zoom]]))

(defn f [camera rectangle]
  (set-zoom! camera
             (calculate-zoom camera
                             rectangle)))
