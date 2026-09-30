(ns gdx.tiled-map-tile-layer-cell
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile
                                        TiledMapTileLayer$Cell)))

(defn create []
  (TiledMapTileLayer$Cell.))

(defn set-tile! [cell tile]
  (.setTile ^TiledMapTileLayer$Cell cell ^TiledMapTile tile))

(defn get-tile [cell]
  (.getTile ^TiledMapTileLayer$Cell cell))
