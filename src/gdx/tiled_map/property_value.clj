(ns gdx.tiled-map.property-value
  (:require [clojure.maps.map-properties.get :refer [props-get]]
            [clojure.maps.tiled.tiled-map-tile :as tile]
            [clojure.maps.tiled.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [clojure.maps.tiled.tiled-map-tile-layer.cell :as cell]))

(defn property-value [layer xy property-key]
  (if-let [cell (get-cell layer xy)]
    (if-let [value (props-get (tile/properties (cell/tile cell)) property-key)]
      value
      :undefined)
    :no-cell))
