(ns clojure.gdx.maps.tiled.tiled-map
  (:require [clojure.gdx.maps.props :as props]
            [clojure.gdx.maps.layers :as layers])
  (:import (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer
                                        TiledMapTileLayer$Cell)))

(defn create []
  (TiledMap.))

(defn properties [^TiledMap tiled-map]
  (.getProperties tiled-map))

(defn layers [^TiledMap tiled-map]
  (.getLayers tiled-map))

(defn- create-layer
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
    (props/put-all! (.getProperties layer) map-properties)
    (doseq [[xy tiled-map-tile] tiles
            :let [[x y] xy]
            :when tiled-map-tile]
      (.setCell layer x y (doto (TiledMapTileLayer$Cell.)
                            (.setTile tiled-map-tile))))
    layer))

(defn add-layer!
  "`properties` is optional. Returns nil."
  [tiled-map
   {:keys [name
           visible?
           properties
           tiles]}]
  (let [props (TiledMap/.getProperties tiled-map) ; shadowing 'properties' otherwise
        layer (create-layer {:width      (props/get props "width")
                             :height     (props/get props "height")
                             :tilewidth  (props/get props "tilewidth")
                             :tileheight (props/get props "tileheight")
                             :name name
                             :visible? visible?
                             :map-properties (doto (props/create)
                                               (props/add! properties))
                             :tiles tiles})]
    (layers/add! (layers tiled-map) layer))
  nil)
