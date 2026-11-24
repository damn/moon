(ns clojure.com.badlogic.gdx.maps.tiled.tiled-map
  "@brief Represents a tiled map, adds the concept of tiles and tilesets."
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn create []
  (TiledMap.))

(defn properties
  "Returns the map's properties"
  [^TiledMap tiled-map]
  (.getProperties tiled-map))

(defn layers
  "Returns the map's layers"
  [^TiledMap tiled-map]
  (.getLayers tiled-map))
