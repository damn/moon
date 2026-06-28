(ns tiled.static-tiled-map-tile.copy
  (:import (com.badlogic.gdx.maps.tiled.tiles StaticTiledMapTile)))

(defn f [^StaticTiledMapTile tile]
  (StaticTiledMapTile. tile))
