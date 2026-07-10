(ns clojure.tile-movement-property
  (:require [gdl.tiled-map-tile-layer-cell :as tiled-map-tile-layer-cell]
            [gdl.tiled-map-tile-layer :as tiled-map-tile-layer]
            [gdl.tiled-map-tile :as tiled-map-tile]
            [gdl.tiled-map :as tiled-map]
            [gdl.map-properties :as map-properties]))

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
