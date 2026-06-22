(ns gdl.tiled-map.spawn-positions
  (:require [gdl.map-layers.get-layer :refer [get-layer]]
            [gdl.map-properties.get :refer [props-get]]
            [gdl.tiled-map.get-layers :refer [get-layers]]
            [gdl.get-properties :refer [get-properties]]
            [gdl.tiled-map-tile-layer.get-width :refer [get-width]]
            [gdl.tiled-map-tile-layer.get-height :refer [get-height]]
            [gdl.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [gdl.tiled-map-tile-layer.cell :as cell]))

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
                          cell/tile
                          get-properties
                          (props-get property-key))]
          :when value]
      [position value])))
