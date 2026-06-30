(ns tiled-map-tile-layer.property-value
  (:require [clojure.gdx :as gdx]))

(defn property-value [layer [x y] property-key]
  (if-let [cell (gdx/tiled-map-tile-layer-get-cell layer x y)]
    (if-let [value (gdx/map-properties-get (gdx/tile-get-properties (gdx/cell-get-tile cell))
                                           property-key)]
      value
      :undefined)
    :no-cell))
