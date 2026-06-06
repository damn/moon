(ns gdx.tiled-map.property-value
  (:require [gdx.maps.properties.get :refer [props-get]]
            [gdx.maps.get-properties :refer [get-properties]]
            [gdx.maps.tiled.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]))

(defn property-value [layer xy property-key]
  (if-let [cell (get-cell layer xy)]
    (if-let [value (props-get (get-properties (cell/tile cell)) property-key)]
      value
      :undefined)
    :no-cell))
