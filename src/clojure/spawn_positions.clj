(ns clojure.spawn-positions
  (:require [clojure.tiled-map-tile-layer$cell :as tiled-map-tile-layer-cell]
            [clojure.tiled-map-tile-layer :as tiled-map-tile-layer]
            [clojure.tiled-map-tile :as tiled-map-tile]
            [clojure.tiled-map :as tiled-map]
            [clojure.map-properties :as map-properties]
            [clojure.get :as get]))

(defn f [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (get/f (tiled-map/get-layers tiled-map) layer-name)]
    (for [x (range (tiled-map-tile-layer/get-width layer))
          y (range (tiled-map-tile-layer/get-height layer))
          :let [position [x y]
                cell (tiled-map-tile-layer/get-cell layer x y)]
          :when cell
          :let [value (map-properties/get (tiled-map-tile/get-properties (tiled-map-tile-layer-cell/get-tile cell))
                            property-key)]
          :when value]
      [position value])))
