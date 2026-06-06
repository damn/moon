(ns gdx.maps.tiled.tiled-map-tile-layer.get-cell
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn get-cell [^TiledMapTileLayer layer [x y]]
  (.getCell layer x y))
