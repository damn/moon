(ns clojure.gdx.static-tiled-map-tile.new-tile
  (:import (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn f [^StaticTiledMapTile tile]
  (StaticTiledMapTile. tile))
