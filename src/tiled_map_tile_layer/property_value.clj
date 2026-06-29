(ns tiled-map-tile-layer.property-value
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn property-value [^TiledMapTileLayer layer [x y] property-key]
  (if-let [cell (.getCell layer x y)]
    (if-let [value (.get (.getProperties (.getTile cell)) property-key)]
      value
      :undefined)
    :no-cell))
