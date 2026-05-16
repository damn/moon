(ns com.badlogic.gdx.maps.tiled.tiled-map-tile-layer
  (:refer-clojure :exclude [name])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn create [width height tilewidth tileheight name visible?]
  (doto (TiledMapTileLayer. width height tilewidth tileheight)
    (.setName name)
    (.setVisible visible?)))

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

(defn visible? [^TiledMapTileLayer layer]
  (.isVisible layer))
