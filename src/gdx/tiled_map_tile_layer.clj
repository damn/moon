(ns gdx.tiled-map-tile-layer
  (:require [gdx.tiled-map-tile :as tiled-map-tile]
            [gdx.tiled-map-tile-layer-cell :as cell])
  (:import (com.badlogic.gdx.maps MapProperties)
           (com.badlogic.gdx.maps.tiled TiledMapTileLayer
                                        TiledMapTileLayer$Cell)))

(defn create-layer [width height tilewidth tileheight]
  (TiledMapTileLayer. (int width) (int height) (int tilewidth) (int tileheight)))

(defn set-name! [layer name]
  (.setName ^TiledMapTileLayer layer ^String name))

(defn set-visible! [layer visible?]
  (.setVisible ^TiledMapTileLayer layer visible?))

(defn get-cell [layer x y]
  (.getCell ^TiledMapTileLayer layer (int x) (int y)))

(defn set-cell! [layer x y cell]
  (.setCell ^TiledMapTileLayer layer (int x) (int y) ^TiledMapTileLayer$Cell cell))

(defn get-properties [layer]
  (.getProperties ^TiledMapTileLayer layer))

(defn get-name [layer]
  (.getName ^TiledMapTileLayer layer))

(defn visible? [layer]
  (.isVisible ^TiledMapTileLayer layer))

(defn get-width [layer]
  (.getWidth ^TiledMapTileLayer layer))

(defn get-height [layer]
  (.getHeight ^TiledMapTileLayer layer))

(defn get-tile-width [layer]
  (.getTileWidth ^TiledMapTileLayer layer))

(defn get-tile-height [layer]
  (.getTileHeight ^TiledMapTileLayer layer))

(defn get-render-offset-x [layer]
  (.getRenderOffsetX ^TiledMapTileLayer layer))

(defn get-render-offset-y [layer]
  (.getRenderOffsetY ^TiledMapTileLayer layer))

(defn property-value [layer [x y] property-key]
  (if-let [cell (get-cell layer x y)]
    (if-let [value (.get ^MapProperties (tiled-map-tile/get-properties (cell/get-tile cell)) property-key)]
      value
      :undefined)
    :no-cell))

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
  (let [layer (doto (create-layer width height tilewidth tileheight)
                (set-name! name)
                (set-visible! visible?))]
    (doseq [[k v] map-properties]
      (assert (string? k))
      (.put ^MapProperties (get-properties layer) k v))
    (doseq [[[x y] tile] tiles
            :when tile]
      (set-cell! layer x y
                 (doto (cell/create)
                   (cell/set-tile! tile))))
    layer))
