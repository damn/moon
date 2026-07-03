(ns clojure.gdx.tiled-map-tile.get-properties
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile)))

(defn f [tile]
  (TiledMapTile/.getProperties tile))
