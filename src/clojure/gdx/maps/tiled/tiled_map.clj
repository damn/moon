(ns clojure.gdx.maps.tiled.tiled-map
  (:require [clojure.gdx.maps.props :as props]
            [clojure.gdx.maps.layers :as layers]
            [clojure.gdx.maps.tiled.layer :as layer])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn create []
  (TiledMap.))

(defn properties [^TiledMap tiled-map]
  (.getProperties tiled-map))

(defn layers [^TiledMap tiled-map]
  (.getLayers tiled-map))

(defn add-layer!
  "`properties` is optional. Returns nil."
  [tiled-map
   {:keys [name
           visible?
           properties
           tiles]}]
  (let [props (TiledMap/.getProperties tiled-map) ; shadowing 'properties' otherwise
        layer (layer/create {:width      (props/get props "width")
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
