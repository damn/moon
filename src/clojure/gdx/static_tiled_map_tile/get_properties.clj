(ns clojure.gdx.static-tiled-map-tile.get-properties
  (:import (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn f [tile]
  (StaticTiledMapTile/.getProperties tile))
