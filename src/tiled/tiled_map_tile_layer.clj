(ns tiled.tiled-map-tile-layer
  (:require [clojure.gdx.map-properties.put! :as put!]
            [clojure.gdx.tiled-map-tile-layer$cell.new :as new-cell]
            [clojure.gdx.tiled-map-tile-layer$cell.set-tile :as set-tile])
  (:import (com.badlogic.gdx.maps.tiled TiledMapTileLayer)))

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
                (.setVisible visible?))]
    (doseq [[k v] map-properties]
      (assert (string? k))
      (put!/f (.getProperties layer) k v))
    (doseq [[[x y] tile] tiles
            :when tile]
      (.setCell ^TiledMapTileLayer layer
                x
                y
                (doto (new-cell/f)
                  (set-tile/f tile))))
    layer))
