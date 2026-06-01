(ns gdx.tiled-map.property-value
  (:require [clojure.gdx.maps.map-properties :as props]
            [clojure.gdx.maps.tiled.tiled-map-tile :as tile]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]))

(defn property-value [layer xy property-key]
  (if-let [cell (layer/cell layer xy)]
    (if-let [value (props/get (tile/properties (cell/tile cell)) property-key)]
      value
      :undefined)
    :no-cell))
