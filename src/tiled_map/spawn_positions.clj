(ns tiled-map.spawn-positions
  (:require [clojure.gdx :as gdx]))

(defn f [tiled-map]
  (let [layer-name "creatures"
        property-key "id"
        layer (gdx/map-layers-get (gdx/tiled-map-get-layers tiled-map) layer-name)]
    (for [x (range (gdx/tiled-map-tile-layer-get-width layer))
          y (range (gdx/tiled-map-tile-layer-get-height layer))
          :let [position [x y]
                cell (gdx/tiled-map-tile-layer-get-cell layer x y)]
          :when cell
          :let [value (gdx/map-properties-get (gdx/tile-get-properties (gdx/cell-get-tile cell))
                                               property-key)]
          :when value]
      [position value])))
