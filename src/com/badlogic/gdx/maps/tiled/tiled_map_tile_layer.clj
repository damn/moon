(ns com.badlogic.gdx.maps.tiled.tiled-map-tile-layer
  (:refer-clojure :exclude [new])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer TiledMapTileLayer$Cell)))

(defn new [width height tilewidth tileheight]
  (TiledMapTileLayer. (int width) (int height) (int tilewidth) (int tileheight)))

(defn getCell [^TiledMapTileLayer layer x y]
  (.getCell layer (int x) (int y)))

(defn getHeight [^TiledMapTileLayer layer]
  (.getHeight layer))

(defn getName [^TiledMapTileLayer layer]
  (.getName layer))

(defn getRenderOffsetX [^TiledMapTileLayer layer]
  (.getRenderOffsetX layer))

(defn getRenderOffsetY [^TiledMapTileLayer layer]
  (.getRenderOffsetY layer))

(defn getTileHeight [^TiledMapTileLayer layer]
  (.getTileHeight layer))

(defn getTileWidth [^TiledMapTileLayer layer]
  (.getTileWidth layer))

(defn getProperties [^TiledMapTileLayer layer]
  (.getProperties layer))

(defn getWidth [^TiledMapTileLayer layer]
  (.getWidth layer))

(defn isVisible [^TiledMapTileLayer layer]
  (.isVisible layer))

(defn setCell [^TiledMapTileLayer layer x y ^TiledMapTileLayer$Cell cell]
  (.setCell layer (int x) (int y) cell))

(defn setName [^TiledMapTileLayer layer ^String name]
  (.setName layer name))

(defn setVisible [^TiledMapTileLayer layer visible?]
  (.setVisible layer visible?))
