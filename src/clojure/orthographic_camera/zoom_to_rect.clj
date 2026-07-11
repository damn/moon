(ns clojure.orthographic-camera.zoom-to-rect
  (:require [moon.orthographic-camera :as moon-orthographic-camera]
            [clojure.orthographic-camera.calculate-zoom :refer [calculate-zoom]]))

(defn f [camera rectangle]
  (moon-orthographic-camera/set-zoom! camera
                                     (calculate-zoom camera
                                                     rectangle)))
