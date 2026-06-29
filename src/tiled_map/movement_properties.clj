(ns tiled-map.movement-properties
  (:require [tiled-map.tile-movement-property :as tile-movement-property]
            [tiled-map.movement-property-layers :as movement-property-layers])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn f [tiled-map position]
  (for [layer (movement-property-layers/f tiled-map)]
    [(TiledMapTileLayer/.getName layer)
     (tile-movement-property/f tiled-map layer position)]))
