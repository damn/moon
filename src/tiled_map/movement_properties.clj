(ns tiled-map.movement-properties
  (:require
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map] [tiled-map.tile-movement-property :as tile-movement-property]
            [tiled-map.movement-property-layers :as movement-property-layers]
            [clojure.gdx.tiled-map-tile-layer.get-name :as get-name]))

(defn f [tiled-map position]
  (for [layer (movement-property-layers/f tiled-map)]
    [(get-name/f layer)
     (tile-movement-property/f tiled-map layer position)]))
