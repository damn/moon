(ns tiled-map.movement-properties
  (:require [tiled-map-tile-layer.get-name :refer [get-name]]
            [tiled-map.tile-movement-property :as tile-movement-property]
            [tiled-map.movement-property-layers :as movement-property-layers]))

(defn f [tiled-map position]
  (for [layer (movement-property-layers/f tiled-map)]
    [(get-name layer)
     (tile-movement-property/f tiled-map layer position)]))
