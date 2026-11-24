(ns clojure.com.badlogic.gdx.maps.tiled.tiled-map-tile-layer-cell
  "@brief represents a cell in a TiledLayer: TiledMapTile, flip and rotation properties."
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile TiledMapTileLayer$Cell)))

(defn create []
  (TiledMapTileLayer$Cell.))

(defn set-tile!
  "Sets the tile to be used for this cell.
  
  - `tile`: the [[clojure.com.badlogic.gdx.maps.tiled.tiled-map-tile]] to use for this cell.
  
  Returns this, for method chaining"
  [^TiledMapTileLayer$Cell cell ^TiledMapTile tile]
  (.setTile cell tile))

(defn get-tile
  "Returns the tile currently assigned to this cell."
  [^TiledMapTileLayer$Cell cell]
  (.getTile cell))
