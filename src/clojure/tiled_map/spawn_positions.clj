(ns clojure.tiled-map.spawn-positions
  (:require [clojure.map-layers.get-layer :refer [get-layer]]
            [clojure.map-properties.get :refer [props-get]]
            [clojure.tiled-map.get-layers :refer [get-layers]]
            [clojure.get-properties :refer [get-properties]]
            [clojure.tiled-map-tile-layer.get-width :refer [get-width]]
            [clojure.tiled-map-tile-layer.get-height :refer [get-height]]
            [clojure.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [clojure.tiled-map-tile-layer.cell :as cell]))

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
