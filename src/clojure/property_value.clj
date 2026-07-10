(ns clojure.property-value
  (:require [gdl.maps.tiled.tiled-map-tile-layer.cell :as tiled-map-tile-layer-cell]
            [gdl.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [gdl.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [gdl.maps.tiled.tiled-map :as tiled-map]
            [gdl.maps.map-properties :as map-properties]))

(defn property-value [layer [x y] property-key]
  (if-let [cell (tiled-map-tile-layer/get-cell layer x y)]
    (if-let [value (map-properties/get (tiled-map-tile/get-properties (tiled-map-tile-layer-cell/get-tile cell)) property-key)]
      value
      :undefined)
    :no-cell))
