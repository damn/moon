(ns gdx.maps.tiled.tiled-map-tile-layer.property-value
  (:require [clojure.tiled-map-tile-layer$cell :as tiled-map-tile-layer-cell]
            [clojure.tiled-map-tile-layer :as tiled-map-tile-layer]
            [clojure.tiled-map-tile :as tiled-map-tile]
            [clojure.tiled-map :as tiled-map]
            [clojure.map-properties :as map-properties]))

(defn property-value [layer [x y] property-key]
  (if-let [cell (tiled-map-tile-layer/get-cell layer x y)]
    (if-let [value (map-properties/get (tiled-map-tile/get-properties (tiled-map-tile-layer-cell/get-tile cell)) property-key)]
      value
      :undefined)
    :no-cell))
