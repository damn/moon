(ns clojure.gdx.tiled-map.layer.cell
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)))

(defn create []
  (TiledMapTileLayer$Cell.))

(defn set-tile! [^TiledMapTileLayer$Cell cell tile]
  (.setTile cell tile))

(defn tile [^TiledMapTileLayer$Cell cell]
  (.getTile cell))
