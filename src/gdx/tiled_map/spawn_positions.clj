(ns gdx.tiled-map.spawn-positions
  (:require [clojure.gdx.maps.map-layers :as layers]
            [clojure.gdx.maps.map-properties.get :refer [props-get]]
            [clojure.gdx.maps.tiled.tiled-map.get-layers :refer [get-layers]]
            [clojure.gdx.maps.tiled.tiled-map-tile :as tile]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.get-width :refer [get-width]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.get-height :refer [get-height]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]))

(defn f [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (layers/get (get-layers tiled-map) layer-name)]
    (for [x (range (get-width layer))
          y (range (get-height layer))
          :let [position [x y]
                cell (get-cell layer position)]
          :when cell
          :let [value (-> cell
                          cell/tile
                          tile/properties
                          (props-get property-key))]
          :when value]
      [position value])))
