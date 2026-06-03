(ns gdx.tiled-map.movement-properties
  (:require [clojure.gdx.maps.tiled.tiled-map-tile-layer.get-name :refer [get-name]]
            [gdx.tiled-map.tile-movement-property :as tile-movement-property]
            [gdx.tiled-map.movement-property-layers :as movement-property-layers]))

(defn f [tiled-map position]
  (for [layer (movement-property-layers/f tiled-map)]
    [(get-name layer)
     (tile-movement-property/f tiled-map layer position)]))
