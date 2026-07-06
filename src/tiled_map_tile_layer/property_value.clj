(ns tiled-map-tile-layer.property-value
  (:require
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.map-properties :as map-properties]
            [clojure.gdx.tiled-map-tile-layer.get-cell :as get-cell]
            [clojure.gdx.tiled-map-tile-layer$cell.get-tile :as get-tile]
            [clojure.gdx.tiled-map-tile.get-properties :as get-tile-properties]))

(defn property-value [layer [x y] property-key]
  (if-let [cell (get-cell/f layer x y)]
    (if-let [value (map-properties/get (get-tile-properties/f (get-tile/f cell)) property-key)]
      value
      :undefined)
    :no-cell))
