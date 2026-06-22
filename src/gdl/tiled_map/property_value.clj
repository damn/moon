(ns gdl.tiled-map.property-value
  (:require [gdl.get :refer [props-get]]
            [gdl.get-properties :refer [get-properties]]
            [gdl.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [gdl.tiled-map-tile-layer.cell :as cell]))

(defn property-value [layer xy property-key]
  (if-let [cell (get-cell layer xy)]
    (if-let [value (props-get (get-properties (cell/tile cell)) property-key)]
      value
      :undefined)
    :no-cell))
