(ns clojure.gdx.maps.tiled.tiled-map-tile-layer
  (:refer-clojure :exclude [name])
  (:require [clojure.tiled-map.props :as props]
            [clojure.tiled-map.tile :as tile]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]))

(def visible? layer/visible?)
(def width layer/width)
(def height layer/height)
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
    (props/add! (layer/properties layer) map-properties)
    (doseq [[pos tile] tiles
            :when tile]
      (layer/set-cell! layer pos (cell/create tile)))
    layer))

(defn property-value [layer pos property-key]
  (if-let [cell (layer/cell layer pos)]
    (if-let [value (props/get (tile/properties (cell/tile cell)) property-key)]
      value
      :undefined)
    :no-cell))
