(ns tiled-map-tile-layer.property-value
  (:require
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.map-properties :as map-properties]
            [clojure.gdx.tiled-map-tile-layer$cell.get-tile :as get-tile]))

(defn property-value [layer [x y] property-key]
  (if-let [cell (tiled-map-tile-layer/get-cell layer x y)]
    (if-let [value (map-properties/get (tiled-map-tile/get-properties (get-tile/f cell)) property-key)]
      value
      :undefined)
    :no-cell))
