(ns com.badlogic.gdx.maps.tiled.tiled-map-tile-layer
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer
                                               TiledMapTileLayer$Cell)))

(defn get-cell [^TiledMapTileLayer layer x y]
  (.getCell layer (int x) (int y)))

(defn get-height [^TiledMapTileLayer layer]
  (.getHeight layer))

(defn get-name [^TiledMapTileLayer layer]
  (TiledMapTileLayer/.getName layer))

(defn get-properties [^TiledMapTileLayer layer]
  (.getProperties layer))

(defn get-width [^TiledMapTileLayer layer]
  (.getWidth layer))

(defn is-visible [^TiledMapTileLayer layer]
  (.isVisible layer))

(defn new [width height tilewidth tileheight]
  (TiledMapTileLayer. (int width) (int height) (int tilewidth) (int tileheight)))

(defn set-cell! [^TiledMapTileLayer layer x y ^TiledMapTileLayer$Cell cell]
  (TiledMapTileLayer/.setCell layer (int x) (int y) cell))

(defn set-name! [^TiledMapTileLayer layer ^String name]
  (TiledMapTileLayer/.setName layer name))

(defn set-visible! [layer visible?]
  (TiledMapTileLayer/.setVisible layer visible?))

(defn visible? [layer]
  (TiledMapTileLayer/.isVisible layer))
