(ns com.badlogic.gdx.maps.tiled.tiled-map
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn get-layers [tiled-map]
  (TiledMap/.getLayers tiled-map))

(defn get-properties [tiled-map]
  (TiledMap/.getProperties tiled-map))

(defn new []
  (TiledMap.))
