(ns com.badlogic.gdx.maps.tiled.tiled-map
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn new []
  (TiledMap.))

(defn getLayers [tiled-map]
  (.getLayers ^TiledMap tiled-map))

(defn getProperties [tiled-map]
  (.getProperties ^TiledMap tiled-map))
