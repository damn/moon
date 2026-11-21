(ns clojure.com.badlogic.gdx.maps.tiled.tiled-map-tile
  (:import (com.badlogic.gdx.maps.tiled TiledMapTile)))

(defn properties [^TiledMapTile tile]
  (.getProperties tile))

(defn texture-region [^TiledMapTile tile]
  (.getTextureRegion tile))

(defn offset-x [^TiledMapTile tile]
  (.getOffsetX tile))

(defn offset-y [^TiledMapTile tile]
  (.getOffsetY tile))
