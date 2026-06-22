(ns gdl.movement-properties
  (:require [gdl.tiled-map-tile-layer.get-name :refer [get-name]]
            [gdl.tile-movement-property :as tile-movement-property]
            [gdl.movement-property-layers :as movement-property-layers]))

(defn f [tiled-map position]
  (for [layer (movement-property-layers/f tiled-map)]
    [(get-name layer)
     (tile-movement-property/f tiled-map layer position)]))
