(ns gdx.tiled-map.property-value
  (:require [com.badlogic.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tile]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]))

(defn property-value [layer xy property-key]
  (if-let [cell (layer/cell layer xy)]
    (if-let [value (props/get (tile/properties (cell/tile cell)) property-key)]
      value
      :undefined)
    :no-cell))
