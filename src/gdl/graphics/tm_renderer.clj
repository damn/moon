(ns gdl.graphics.tm-renderer
  (:require [moon.color :as color]
            [gdl.maps.map-layers :as layers]
            [gdl.maps.tiled :as tiled-map]
            [gdl.maps.tiled.layer :as layer]
            [gdl.utils.viewport :as viewport])
  (:import (moon TiledMapRenderer
                 TiledMapRenderer$ColorSetter)))

(defn draw! [tiled-map-renderer world-viewport tiled-map color-setter]
  (let [^TiledMapRenderer renderer (tiled-map-renderer tiled-map)
        camera (viewport/camera world-viewport)]
    (.setColorSetter renderer (reify TiledMapRenderer$ColorSetter
                                (apply [_ color x y]
                                  (color/float-bits (color-setter color x y)))))
    (.setView renderer camera)
    (->> tiled-map
         tiled-map/layers
         (filter layer/visible?)
         (map (partial layers/get-index (tiled-map/layers tiled-map)))
         int-array
         (.render renderer))))

(defn create [world-unit-scale batch]
  (memoize (fn [tiled-map]
             (TiledMapRenderer. tiled-map (float world-unit-scale) batch))))
