(ns com.badlogic.gdx.maps.tiled.tiled-map-tile
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile
                                         TiledMapTileLayer$Cell)))

(defn get-offset-x [tile]
  (TiledMapTile/.getOffsetX tile))

(defn get-offset-y [tile]
  (TiledMapTile/.getOffsetY tile))

(defn get-properties [tile]
  (TiledMapTile/.getProperties tile))

(defn get-texture-region [tile]
  (TiledMapTile/.getTextureRegion tile))

(defn get-tile [^TiledMapTileLayer$Cell cell]
  (.getTile cell))
