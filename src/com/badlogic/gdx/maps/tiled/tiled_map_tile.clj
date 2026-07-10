(ns com.badlogic.gdx.maps.tiled.tiled-map-tile
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile TiledMapTileLayer$Cell)))

(defn getOffsetX [tile]
  (.getOffsetX ^TiledMapTile tile))

(defn getOffsetY [tile]
  (.getOffsetY ^TiledMapTile tile))

(defn getProperties [tile]
  (.getProperties ^TiledMapTile tile))

(defn getTextureRegion [tile]
  (.getTextureRegion ^TiledMapTile tile))

(defn getTile [^TiledMapTileLayer$Cell cell]
  (.getTile cell))
