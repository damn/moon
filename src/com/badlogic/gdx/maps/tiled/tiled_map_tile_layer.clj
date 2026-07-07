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

(defn get-render-offset-x [^TiledMapTileLayer layer]
  (.getRenderOffsetX layer))

(defn get-render-offset-y [^TiledMapTileLayer layer]
  (.getRenderOffsetY layer))

(defn get-tile-height [^TiledMapTileLayer layer]
  (.getTileHeight layer))

(defn get-tile-width [^TiledMapTileLayer layer]
  (.getTileWidth layer))

(defn get-properties [^TiledMapTileLayer layer]
  (.getProperties layer))

(defn get-width [^TiledMapTileLayer layer]
  (.getWidth layer))

(defn visible? [^TiledMapTileLayer layer]
  (TiledMapTileLayer/.isVisible layer))

(defn new [width height tilewidth tileheight]
  (TiledMapTileLayer. (int width) (int height) (int tilewidth) (int tileheight)))

(defn set-cell! [^TiledMapTileLayer layer x y ^TiledMapTileLayer$Cell cell]
  (TiledMapTileLayer/.setCell layer (int x) (int y) cell))

(defn set-name! [^TiledMapTileLayer layer ^String name]
  (TiledMapTileLayer/.setName layer name))

(defn set-visible! [layer visible?]
  (TiledMapTileLayer/.setVisible layer visible?))
