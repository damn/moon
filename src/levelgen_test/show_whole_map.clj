(ns levelgen-test.show-whole-map
  (:require [clojure.gdx.maps.map-properties :as props]
            [clojure.gdx.maps.tiled.tiled-map :as tiled-map]
            [gdx.graphics.orthographic-camera :as camera]))

(defn f!
  [{:keys [ctx/camera
           ctx/tiled-map]}]
  (camera/set-position! camera
                        [(/ (props/get (tiled-map/props tiled-map) "width") 2)
                         (/ (props/get (tiled-map/props tiled-map) "height") 2)])
  (camera/set-zoom! camera
                    (camera/calculate-zoom camera
                                           {:left [0 0]
                                            :top [0 (props/get (tiled-map/props tiled-map) "height")]
                                            :right [(props/get (tiled-map/props tiled-map) "width") 0]
                                            :bottom [0 0]})))
