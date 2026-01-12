(ns moon.tm-renderer
  (:require [clj.api.com.badlogic.gdx.graphics.color :as color])
  (:import (com.badlogic.gdx.maps MapLayers)
           (com.badlogic.gdx.maps.tiled TiledMapTileLayer)
           (com.badlogic.gdx.utils.viewport Viewport)
           (moon TiledMapRenderer
                 TiledMapRenderer$ColorSetter)))

(defn draw! [tiled-map-renderer world-viewport tiled-map color-setter]
  (let [^TiledMapRenderer renderer (tiled-map-renderer tiled-map)
        camera (Viewport/.getCamera world-viewport)]
    (.setColorSetter renderer (reify TiledMapRenderer$ColorSetter
                                (apply [_ color x y]
                                  (color/float-bits (color-setter color x y)))))
    (.setView renderer camera)
    (->> tiled-map
         .getLayers
         (filter TiledMapTileLayer/.isVisible)
         (map (partial MapLayers/.getIndex (.getLayers tiled-map)))
         int-array
         (.render renderer))))

(defn create [world-unit-scale batch]
  (memoize (fn [tiled-map]
             (TiledMapRenderer. tiled-map (float world-unit-scale) batch))))
