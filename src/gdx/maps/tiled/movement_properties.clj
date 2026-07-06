(ns gdx.maps.tiled.movement-properties
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [gdx.maps.tiled.tile-movement-property :as tile-movement-property]
            [gdx.maps.tiled.movement-property-layers :as movement-property-layers]))

(defn f [tiled-map position]
  (for [layer (movement-property-layers/f tiled-map)]
    [(tiled-map-tile-layer/get-name layer)
     (tile-movement-property/f tiled-map layer position)]))
