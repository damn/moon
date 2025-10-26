(ns moon.world.tiled-map
  (:require [moon.maps.map-layers :as layers]
            [moon.maps.map-properties :as props]
            [moon.maps.tiled :as tiled-map]
            [moon.maps.tiled.tiled-map-tile :as tile]
            [moon.maps.tiled.layer :as layer]
            [moon.maps.tiled.layer.cell :as cell]))

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
                          cell/tile
                          tile/properties
                          (props/get property-key))]
          :when value]
      [position value])))
