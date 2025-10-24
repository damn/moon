(ns clojure.gdx.maps.tiled.layer
  (:refer-clojure :exclude [name])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn create [width height tilewidth tileheight]
  (TiledMapTileLayer. width height tilewidth tileheight))

(defn set-name! [^TiledMapTileLayer layer name]
  (.setName layer name))

(defn set-visible! [^TiledMapTileLayer layer boolean]
  (.setVisible layer boolean))

(defn visible? [^TiledMapTileLayer layer]
  (.isVisible layer))

(defn properties [^TiledMapTileLayer layer]
  (.getProperties layer))

(defn name [^TiledMapTileLayer layer]
  (.getName layer))

(defn cell [^TiledMapTileLayer layer [x y]]
  (.getCell layer x y))

(defn set-cell! [^TiledMapTileLayer layer [x y] cell]
  (.setCell layer x y cell))

(defn width [^TiledMapTileLayer layer]
  (.getWidth layer))

(defn height [^TiledMapTileLayer layer]
  (.getHeight layer))
