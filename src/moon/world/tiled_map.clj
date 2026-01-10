(ns moon.world.tiled-map
  (:require [gdl.maps.map-layers :as layers]
            [gdl.maps.map-properties :as props]
            [gdl.maps.tiled :as tiled-map]
            [gdl.maps.tiled.layer :as layer]
            [gdl.maps.tiled.tiled-map-tile :as tile]))

(defn spawn-positions
  [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (layers/get (tiled-map/layers tiled-map) layer-name)]
    (for [x (range (layer/width layer))
          y (range (layer/height layer))
          :let [position [x y]
                cell (layer/cell layer position)]
          :when cell
          :let [value (-> cell
                          .getTile
                          tile/properties
                          (props/get property-key))]
          :when value]
      [position value])))
