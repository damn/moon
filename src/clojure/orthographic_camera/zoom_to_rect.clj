(ns clojure.orthographic-camera.zoom-to-rect
  (:require [gdx.graphics.orthographic-camera :as gdx-orthographic-camera]
            [clojure.orthographic-camera.calculate-zoom :refer [calculate-zoom]]))

(defn f [camera rectangle]
  (gdx-orthographic-camera/set-zoom! camera
                                     (calculate-zoom camera
                                                     rectangle)))
