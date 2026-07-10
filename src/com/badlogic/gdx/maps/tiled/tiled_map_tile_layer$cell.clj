(ns com.badlogic.gdx.maps.tiled.tiled-map-tile-layer$cell
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile TiledMapTileLayer$Cell)))

(defn new []
  (TiledMapTileLayer$Cell.))

(defn getTile [cell]
  (.getTile ^TiledMapTileLayer$Cell cell))

(defn setTile [cell tile]
  (.setTile ^TiledMapTileLayer$Cell cell ^TiledMapTile tile))
