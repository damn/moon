(ns moon.tiled-map-renderer
  (:import (com.badlogic.gdx.maps MapLayers)
           (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer)
           (com.badlogic.gdx.utils.viewport Viewport)
           (moon TiledMapRenderer
                 TiledMapRenderer$ColorSetter)))

(defn draw! [batch world-unit-scale camera ^TiledMap tiled-map color-setter]
  (let [renderer (doto (TiledMapRenderer. tiled-map (float world-unit-scale) batch)
                   (.setColorSetter (reify TiledMapRenderer$ColorSetter
                                      (apply [_ color x y]
                                        (color-setter color x y))))
                   (.setView camera))
        layers (.getLayers tiled-map)]
    (->> layers
         (filter TiledMapTileLayer/.isVisible)
         (map #(.getIndex layers ^TiledMapTileLayer %))
         int-array
         (.render renderer))))
