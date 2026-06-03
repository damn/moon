(ns gdx.tiled-map.property-value
  (:require [clojure.gdx.maps.map-properties.get :refer [props-get]]
            [clojure.gdx.maps.tiled.tiled-map-tile :as tile]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]))

(defn property-value [layer xy property-key]
  (if-let [cell (get-cell layer xy)]
    (if-let [value (props-get (tile/properties (cell/tile cell)) property-key)]
      value
      :undefined)
    :no-cell))
