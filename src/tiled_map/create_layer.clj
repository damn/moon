(ns tiled-map.create-layer
  (:require [tiled.tiled-map-tile-layer :as tiled-map-tile-layer])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn f
  [^TiledMap tiled-map
   {:keys [name
           visible?
           properties
           tiles]}]
  (let [props (.getProperties tiled-map)]
    (tiled-map-tile-layer/f
     {:width      (.get props "width")
      :height     (.get props "height")
      :tilewidth  (.get props "tilewidth")
      :tileheight (.get props "tileheight")
      :name name
      :visible? visible?
      :map-properties properties
      :tiles tiles})))
