(ns clojure.tile-movement-property
  (:require [clojure.tiled-map-tile-layer$cell :as tiled-map-tile-layer-cell]
            [clojure.tiled-map-tile-layer :as tiled-map-tile-layer]
            [clojure.tiled-map-tile :as tiled-map-tile]
            [clojure.tiled-map :as tiled-map]
            [clojure.map-properties :as map-properties]))

(defn f
  [tiled-map layer [x y]]
  (let [position [x y]]
    (when-let [cell (tiled-map-tile-layer/get-cell layer x y)]
      (let [value (map-properties/get (tiled-map-tile/get-properties (tiled-map-tile-layer-cell/get-tile cell))
                         "movement")]
        (assert value
                (str "Value for :movement at position "
                     position  " / mapeditor inverted position: " [(position 0)
                                                                   (- (dec (map-properties/get (tiled-map/get-properties tiled-map) "height"))
                                                                      (position 1))]
                     " and layer " (tiled-map-tile-layer/get-name layer) " is undefined."))
        value))))
