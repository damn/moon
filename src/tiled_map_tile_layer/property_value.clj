(ns tiled-map-tile-layer.property-value
  (:require [clojure.gdx.map-properties.get :as get]
            [clojure.gdx.tiled-map-tile-layer.get-cell :as get-cell]
            [clojure.gdx.tiled-map-tile-layer$cell.get-tile :as get-tile]
            [clojure.gdx.tiled-map-tile.get-properties :as get-tile-properties]))

(defn property-value [layer [x y] property-key]
  (if-let [cell (get-cell/f layer x y)]
    (if-let [value (get/f (get-tile-properties/f (get-tile/f cell)) property-key)]
      value
      :undefined)
    :no-cell))
