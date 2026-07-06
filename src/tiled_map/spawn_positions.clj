(ns tiled-map.spawn-positions
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer$cell :as tiled-map-tile-layer-cell]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.map-properties :as map-properties]
            [com.badlogic.gdx.maps.map-layers :as map-layers]))

(defn f [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (map-layers/get (tiled-map/get-layers tiled-map) layer-name)]
    (for [x (range (tiled-map-tile-layer/get-width layer))
          y (range (tiled-map-tile-layer/get-height layer))
          :let [position [x y]
                cell (tiled-map-tile-layer/get-cell layer x y)]
          :when cell
          :let [value (map-properties/get (tiled-map-tile/get-properties (tiled-map-tile-layer-cell/get-tile cell))
                            property-key)]
          :when value]
      [position value])))
