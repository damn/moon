(ns tiled-map-tile-layer.property-value
  (:require [map-properties.get :as get]
            [tiled-map-tile.get-properties :as get-properties]
            [tiled-map-tile-layer.get-cell :refer [get-cell]]
            [gdl.get-tile :as get-tile]))

(defn property-value [layer xy property-key]
  (if-let [cell (get-cell layer xy)]
    (if-let [value (get/f (get-properties/f (get-tile/f cell)) property-key)]
      value
      :undefined)
    :no-cell))
