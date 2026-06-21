(ns clojure.tiled-map.movement-properties
  (:require [clojure.tiled-map-tile-layer.get-name :refer [get-name]]
            [clojure.tiled-map.tile-movement-property :as tile-movement-property]
            [clojure.tiled-map.movement-property-layers :as movement-property-layers]))

(defn f [tiled-map position]
  (for [layer (movement-property-layers/f tiled-map)]
    [(get-name layer)
     (tile-movement-property/f tiled-map layer position)]))
