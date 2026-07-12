(ns clojure.gdx.maps.tiled.tiled-map-tile-layer
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer$cell :as tiled-map-tile-layer-cell]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [clojure.gdx.maps.map-properties :as map-properties]))

(defn property-value [layer [x y] property-key]
  (if-let [cell (tiled-map-tile-layer/getCell layer x y)]
    (if-let [value (map-properties/get (tiled-map-tile/getProperties (tiled-map-tile-layer-cell/getTile cell)) property-key)]
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
  (let [layer (doto (tiled-map-tile-layer/new width height tilewidth tileheight)
                (tiled-map-tile-layer/setName name)
                (tiled-map-tile-layer/setVisible visible?))]
    (doseq [[k v] map-properties]
      (assert (string? k))
      (map-properties/put! (tiled-map-tile-layer/getProperties layer) k v))
    (doseq [[[x y] tile] tiles
            :when tile]
      (tiled-map-tile-layer/setCell layer x y
                  (doto (tiled-map-tile-layer-cell/new)
                    (tiled-map-tile-layer-cell/setTile tile))))
    layer))
