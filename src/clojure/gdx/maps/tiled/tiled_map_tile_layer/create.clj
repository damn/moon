(ns clojure.gdx.maps.tiled.tiled-map-tile-layer.create
  (:require [clojure.put :refer [put!]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.get-properties :refer [get-properties]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.set-visible :refer [set-visible!]])
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
                (set-visible! visible?))]
    (doseq [[k v] map-properties]
      (assert (string? k))
      (put! (get-properties layer) k v))
    (doseq [[[x y] tile] tiles
            :when tile]
      (.setCell layer x y (doto (TiledMapTileLayer$Cell.)
                            (.setTile tile))))
    layer))
