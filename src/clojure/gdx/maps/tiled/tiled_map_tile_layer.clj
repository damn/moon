(ns clojure.gdx.maps.tiled.tiled-map-tile-layer
  (:refer-clojure :exclude [name])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn visible? [^TiledMapTileLayer layer]
  (.isVisible layer))

(defn properties [^TiledMapTileLayer layer]
  (.getProperties layer))

(defn cell [^TiledMapTileLayer layer [x y]]
  (.getCell layer x y))

(defn name [^TiledMapTileLayer layer]
  (.getName layer))

(defn width [^TiledMapTileLayer layer]
  (.getWidth layer))

(defn height [^TiledMapTileLayer layer]
  (.getHeight layer))

(defn set-visible! [^TiledMapTileLayer layer bool]
  (.setVisible layer bool))

(defn set-cell! [^TiledMapTileLayer layer [x y] cell]
  (.setCell layer x y cell))
