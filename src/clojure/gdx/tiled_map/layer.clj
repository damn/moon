(ns clojure.gdx.tiled-map.layer
  (:refer-clojure :exclude [name])
  (:require [clojure.gdx.maps.props :as props]
            [clojure.gdx.tiled-map.layer.cell :as cell])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

(defn visible? [^TiledMapTileLayer layer]
  (.isVisible layer))

(defn cell [^TiledMapTileLayer layer [x y]]
  (.getCell layer x y))

(defn name [^TiledMapTileLayer layer]
  (.getName layer))

(defn properties [^TiledMapTileLayer layer]
  (.getProperties layer))

(defn width [^TiledMapTileLayer layer]
  (.getWidth layer))

(defn height [^TiledMapTileLayer layer]
  (.getHeight layer))

(defn create
  [{:keys [width
           height
           tilewidth
           tileheight
           name
           visible?
           map-properties
           tiles]}]
  {:pre [(string? name)
         (boolean? visible?)]}
  (let [layer (doto (TiledMapTileLayer. width height tilewidth tileheight)
                (.setName name)
                (.setVisible visible?))]
    (props/put-all! (properties layer) map-properties)
    (doseq [[xy tiled-map-tile] tiles
            :let [[x y] xy]
            :when tiled-map-tile]
      (.setCell layer x y (doto (cell/create)
                            (cell/set-tile! tiled-map-tile))))
    layer))
