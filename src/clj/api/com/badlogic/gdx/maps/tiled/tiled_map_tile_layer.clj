(ns clj.api.com.badlogic.gdx.maps.tiled.tiled-map-tile-layer
  (:refer-clojure :exclude [name])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn create [width height tilewidth tileheight]
  (TiledMapTileLayer. width height tilewidth tileheight))

(defn set-name! [^TiledMapTileLayer layer name]
  (.setName layer name))

(defn set-visible! [^TiledMapTileLayer layer visible?]
  (.setVisible layer visible?))

(defn set-cell! [^TiledMapTileLayer layer [x y] cell]
  (.setCell layer x y cell))

(defn name [^TiledMapTileLayer layer]
  (.getName layer))

(defn visible? [^TiledMapTileLayer layer]
  (.isVisible layer))

(defn cell [^TiledMapTileLayer layer [x y]]
  (.getCell layer x y))

(defn properties [^TiledMapTileLayer layer]
  (.getProperties layer))

(defn width [^TiledMapTileLayer layer]
  (.getWidth layer))

(defn height [^TiledMapTileLayer layer]
  (.getHeight layer))
