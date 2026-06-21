(ns levelgen-test.show-whole-map
  (:require [clojure.maps.properties.get :refer [props-get]]
            [clojure.maps.get-properties :refer [get-properties]]
            [clojure.graphics.orthographic-camera.set-position :refer [set-position!]]
            [orthographic-camera.calculate-zoom :refer [calculate-zoom]]
            [clojure.graphics.orthographic-camera.set-zoom :refer [set-zoom!]]))

(defn f!
  [{:keys [ctx/camera
           ctx/tiled-map]}]
  (set-position! camera
                 [(/ (props-get (get-properties tiled-map) "width") 2)
                  (/ (props-get (get-properties tiled-map) "height") 2)])
  (set-zoom! camera
             (calculate-zoom camera
                             {:left [0 0]
                              :top [0 (props-get (get-properties tiled-map) "height")]
                              :right [(props-get (get-properties tiled-map) "width") 0]
                              :bottom [0 0]})))
