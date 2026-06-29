(ns tiled-map.spawn-positions
  (:require [tiled-map.get-layers :refer [get-layers]])
  (:import (com.badlogic.gdx.maps MapLayers
                                  MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)))

(defn f [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (MapLayers/.get (get-layers tiled-map) layer-name)]
    (for [x (range (.getWidth layer))
          y (range (.getHeight layer))
          :let [position [x y]
                cell (.getCell layer x y)]
          :when cell
          :let [value (MapProperties/.get (.getProperties (.getTile ^TiledMapTileLayer$Cell cell))
                                          property-key)]
          :when value]
      [position value])))
