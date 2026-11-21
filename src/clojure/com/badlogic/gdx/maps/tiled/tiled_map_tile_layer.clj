(ns clojure.com.badlogic.gdx.maps.tiled.tiled-map-tile-layer
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer TiledMapTileLayer$Cell))
  (:refer-clojure :exclude [name]))

(defn create [width height tilewidth tileheight]
  (TiledMapTileLayer. (int width)
                      (int height)
                      (int tilewidth)
                      (int tileheight)))

(defn properties [^TiledMapTileLayer layer]
  (.getProperties layer))

(defn width [^TiledMapTileLayer layer]
  (.getWidth layer))

(defn height [^TiledMapTileLayer layer]
  (.getHeight layer))

(defn name [^TiledMapTileLayer layer]
  (.getName layer))

(defn set-name! [^TiledMapTileLayer layer name]
  (.setName layer name))

(defn visible? [^TiledMapTileLayer layer]
  (.isVisible layer))

(defn set-visible! [^TiledMapTileLayer layer v]
  (.setVisible layer v))

(defn get-cell [^TiledMapTileLayer layer x y]
  (.getCell layer (int x) (int y)))

(defn set-cell! [^TiledMapTileLayer layer x y ^TiledMapTileLayer$Cell cell]
  (.setCell layer (int x) (int y) cell))

(defn tile-width [^TiledMapTileLayer layer]
  (.getTileWidth layer))

(defn tile-height [^TiledMapTileLayer layer]
  (.getTileHeight layer))

(defn render-offset-x [^TiledMapTileLayer layer]
  (.getRenderOffsetX layer))

(defn render-offset-y [^TiledMapTileLayer layer]
  (.getRenderOffsetY layer))
