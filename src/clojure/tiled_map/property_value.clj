(ns clojure.tiled-map.property-value
  (:require [clojure.map-properties.get :refer [props-get]]
            [clojure.get-properties :refer [get-properties]]
            [clojure.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [clojure.tiled-map-tile-layer.cell :as cell]))

(defn property-value [layer xy property-key]
  (if-let [cell (get-cell layer xy)]
    (if-let [value (props-get (get-properties (cell/tile cell)) property-key)]
      value
      :undefined)
    :no-cell))
