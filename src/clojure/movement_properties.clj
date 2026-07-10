(ns clojure.movement-properties
  (:require [gdl.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [gdl.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [gdl.maps.tiled.tiled-map :as tiled-map]
            [clojure.tile-movement-property :as tile-movement-property]
            [clojure.movement-property-layers :as movement-property-layers]))

(defn f [tiled-map position]
  (for [layer (movement-property-layers/f tiled-map)]
    [(tiled-map-tile-layer/get-name layer)
     (tile-movement-property/f tiled-map layer position)]))
