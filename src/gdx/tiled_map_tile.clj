(ns gdx.tiled-map-tile
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile)))

(defn get-properties [tile]
  (.getProperties ^TiledMapTile tile))

(defn get-texture-region [tile]
  (.getTextureRegion ^TiledMapTile tile))

(defn get-offset-x [tile]
  (.getOffsetX ^TiledMapTile tile))

(defn get-offset-y [tile]
  (.getOffsetY ^TiledMapTile tile))
