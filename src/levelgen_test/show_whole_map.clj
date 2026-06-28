(ns levelgen-test.show-whole-map
  (:require [tiled-map.get-properties :as get-properties]
            [orthographic-camera.set-position :refer [set-position!]]
            [orthographic-camera.calculate-zoom :refer [calculate-zoom]]
            [orthographic-camera.set-zoom :refer [set-zoom!]])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn f!
  [{:keys [ctx/camera
           ctx/tiled-map]}]
  (set-position! camera
                 [(/ (MapProperties/.get (get-properties/f tiled-map) "width") 2)
                  (/ (MapProperties/.get (get-properties/f tiled-map) "height") 2)])
  (set-zoom! camera
             (calculate-zoom camera
                             {:left [0 0]
                              :top [0 (MapProperties/.get (get-properties/f tiled-map) "height")]
                              :right [(MapProperties/.get (get-properties/f tiled-map) "width") 0]
                              :bottom [0 0]})))
