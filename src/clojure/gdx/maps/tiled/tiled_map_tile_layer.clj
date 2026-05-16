(ns clojure.gdx.maps.tiled.tiled-map-tile-layer
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]))

(def properties layer/properties)
(def name layer/name)
(def cell layer/cell)

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
  (let [layer (layer/create width height tilewidth tileheight name visible?)]
    (.putAll (layer/properties layer) map-properties)
    (doseq [[pos tile] tiles
            :when tile]
      (layer/set-cell! layer pos (cell/create tile)))
    layer))

(defn property-value [layer [x y] property-key]
  (if-let [cell (layer/cell layer x y)]
    (if-let [value (.get (.getProperties (cell/tile cell)) property-key)]
      value
      :undefined)
    :no-cell))
