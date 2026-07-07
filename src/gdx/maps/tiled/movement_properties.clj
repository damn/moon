(ns gdx.maps.tiled.movement-properties
  (:require [clojure.tiled-map-tile-layer :as tiled-map-tile-layer]
            [clojure.tiled-map-tile :as tiled-map-tile]
            [clojure.tiled-map :as tiled-map]
            [gdx.maps.tiled.tile-movement-property :as tile-movement-property]
            [gdx.maps.tiled.movement-property-layers :as movement-property-layers]))

(defn f [tiled-map position]
  (for [layer (movement-property-layers/f tiled-map)]
    [(tiled-map-tile-layer/get-name layer)
     (tile-movement-property/f tiled-map layer position)]))
