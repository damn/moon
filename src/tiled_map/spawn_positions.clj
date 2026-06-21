(ns tiled-map.spawn-positions
  (:require [com.badlogic.gdx.maps.layers.get-layer :refer [get-layer]]
            [com.badlogic.gdx.maps.properties.get :refer [props-get]]
            [com.badlogic.gdx.maps.tiled.tiled-map.get-layers :refer [get-layers]]
            [com.badlogic.gdx.maps.get-properties :refer [get-properties]]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.get-width :refer [get-width]]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.get-height :refer [get-height]]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]))

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
