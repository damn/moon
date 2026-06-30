(ns tiled-map.movement-properties
  (:require [tiled-map.movement-property-layers :as movement-property-layers]
            [tiled-map.tile-movement-property :as tile-movement-property]))

(defn f [tiled-map position]
  (for [layer (movement-property-layers/f tiled-map)]
    (tile-movement-property/f tiled-map layer position)))
