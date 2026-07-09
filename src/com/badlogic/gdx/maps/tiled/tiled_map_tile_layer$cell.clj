(ns com.badlogic.gdx.maps.tiled.tiled-map-tile-layer$cell
  (:refer-clojure :exclude [new])
  (:import
           (com.badlogic.gdx.maps.tiled TiledMapTileLayer$Cell)
           ))

(defn get-tile [cell]
  (TiledMapTileLayer$Cell/.getTile cell))

(defn new []
  (TiledMapTileLayer$Cell.))

(defn set-tile! [cell tile]
  (TiledMapTileLayer$Cell/.setTile cell tile))
