(ns tiled-map-tile-layer.property-value
  (:require [tiled-map-tile-layer.get-cell :refer [get-cell]])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)))

(defn property-value [layer xy property-key]
  (if-let [cell (get-cell layer xy)]
    (if-let [value (.get (.getProperties (.getTile ^TiledMapTileLayer$Cell cell)) property-key)]
      value
      :undefined)
    :no-cell))
