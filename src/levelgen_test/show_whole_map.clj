(ns levelgen-test.show-whole-map
  (:require [clojure.maps.map-properties.get :refer [props-get]]
            [clojure.get-properties :refer [get-properties]]
            [gdx.graphics.orthographic-camera :as camera]))

(defn f!
  [{:keys [ctx/camera
           ctx/tiled-map]}]
  (camera/set-position! camera
                        [(/ (props-get (get-properties tiled-map) "width") 2)
                         (/ (props-get (get-properties tiled-map) "height") 2)])
  (camera/set-zoom! camera
                    (camera/calculate-zoom camera
                                           {:left [0 0]
                                            :top [0 (props-get (get-properties tiled-map) "height")]
                                            :right [(props-get (get-properties tiled-map) "width") 0]
                                            :bottom [0 0]})))
