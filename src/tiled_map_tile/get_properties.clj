(ns tiled-map-tile.get-properties
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile)))

(defn f [^TiledMapTile tile]
  (.getProperties tile))
