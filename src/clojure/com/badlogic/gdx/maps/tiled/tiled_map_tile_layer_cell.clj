(ns clojure.com.badlogic.gdx.maps.tiled.tiled-map-tile-layer-cell
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile TiledMapTileLayer$Cell)))

(defn create []
  (TiledMapTileLayer$Cell.))

(defn set-tile! [^TiledMapTileLayer$Cell cell ^TiledMapTile tile]
  (.setTile cell tile))

(defn get-tile [^TiledMapTileLayer$Cell cell]
  (.getTile cell))
