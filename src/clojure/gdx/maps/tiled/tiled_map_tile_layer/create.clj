(ns clojure.gdx.maps.tiled.tiled-map-tile-layer.create
  (:require [clojure.gdx.maps.map-properties :as props]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer :as layer])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer
                                        TiledMapTileLayer$Cell)))

(defn f
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
                (layer/set-visible! visible?))]
    (doseq [[k v] map-properties]
      (assert (string? k))
      (props/put! (layer/properties layer) k v))
    (doseq [[pos tile] tiles
            :when tile]
      (layer/set-cell! layer pos (doto (TiledMapTileLayer$Cell.)
                                   (.setTile tile))))
    layer))
