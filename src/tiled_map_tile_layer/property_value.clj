(ns tiled-map-tile-layer.property-value
  (:require [map-properties.get :as get]
            [tiled-map-tile.get-properties :as get-properties]
            [tiled-map-tile-layer.get-cell :refer [get-cell]]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer-cell :as cell]))

(defn property-value [layer xy property-key]
  (if-let [cell (get-cell layer xy)]
    (if-let [value (get/f (get-properties/f (cell/tile cell)) property-key)]
      value
      :undefined)
    :no-cell))
