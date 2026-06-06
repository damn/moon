(ns gdx.maps.tiled.tiled-map-tile-layer.cell
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)))

(defn tile [^TiledMapTileLayer$Cell cell]
  (.getTile cell))
