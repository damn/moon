(ns com.badlogic.gdx.maps.tiled.tiled-map
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn create []
  (TiledMap.))

(defn get-properties [^TiledMap tiled-map]
  (.getProperties tiled-map))

(defn get-layers [^TiledMap tiled-map]
  (.getLayers tiled-map))

(defn type-hint
  ^TiledMap
  [obj]
  obj)
