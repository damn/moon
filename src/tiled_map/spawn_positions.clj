(ns tiled-map.spawn-positions
  (:require [map-layers.get-layer :refer [get-layer]]
            [map-properties.get :as get]
            [tiled-map.get-layers :refer [get-layers]]
            [tiled-map-tile-layer-cell.get-tile :as get-tile]
            [tiled-map-tile.get-properties :as get-properties]
            [tiled-map-tile-layer.get-width :refer [get-width]]
            [tiled-map-tile-layer.get-height :refer [get-height]]
            [tiled-map-tile-layer.get-cell :refer [get-cell]]))

(defn f [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (get-layer (get-layers tiled-map) layer-name)]
    (for [x (range (get-width layer))
          y (range (get-height layer))
          :let [position [x y]
                cell (get-cell layer position)]
          :when cell
          :let [value (-> cell
                          get-tile/f
                          get-properties/f
                          (get/f property-key))]
          :when value]
      [position value])))
