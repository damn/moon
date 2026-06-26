(ns com.badlogic.gdx.maps.tiled.tiled-map-tile-layer
  (:refer-clojure :exclude [name])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn create [width height tilewidth tileheight]
  (TiledMapTileLayer. width height tilewidth tileheight))

(defn set-name! [^TiledMapTileLayer layer name]
  (.setName layer name))

(defn set-cell! [^TiledMapTileLayer layer x y cell]
  (.setCell layer x y cell))

(defn visible? [^TiledMapTileLayer layer]
  (.isVisible layer))

(defn width [^TiledMapTileLayer layer]
  (.getWidth layer))

(defn height [^TiledMapTileLayer layer]
  (.getHeight layer))

(defn tile-width [^TiledMapTileLayer layer]
  (.getTileWidth layer))

(defn tile-height [^TiledMapTileLayer layer]
  (.getTileHeight layer))

(defn render-offset-x [^TiledMapTileLayer layer]
  (.getRenderOffsetX layer))

(defn render-offset-y [^TiledMapTileLayer layer]
  (.getRenderOffsetY layer))

(defn cell [^TiledMapTileLayer layer x y]
  (.getCell layer x y))

(defn name [^TiledMapTileLayer layer]
  (.getName layer))

(defn properties [^TiledMapTileLayer layer]
  (.getProperties layer))

(defn set-visible! [^TiledMapTileLayer layer bool]
  (.setVisible layer bool))
