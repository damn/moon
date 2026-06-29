(ns levelgen-test.show-whole-map
  (:require [orthographic-camera.set-position :refer [set-position!]]
            [orthographic-camera.calculate-zoom :refer [calculate-zoom]]
            [orthographic-camera.set-zoom :refer [set-zoom!]])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn f!
  [{:keys [ctx/camera
           ctx/tiled-map]}]
  (set-position! camera
                 [(/ (.get (TiledMap/.getProperties tiled-map) "width") 2)
                  (/ (.get (TiledMap/.getProperties tiled-map) "height") 2)])
  (set-zoom! camera
             (calculate-zoom camera
                             {:left [0 0]
                              :top [0 (.get (TiledMap/.getProperties tiled-map) "height")]
                              :right [(.get (TiledMap/.getProperties tiled-map) "width") 0]
                              :bottom [0 0]})))
