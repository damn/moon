(ns gdx.maps.tiled.movement-property
  (:require [gdx.maps.tiled.movement-property-layers :as movement-property-layers]
            [gdx.maps.tiled.tile-movement-property :as tile-movement-property]))

(defn f [tiled-map position]
  (or (->> tiled-map
           movement-property-layers/f
           (some #(tile-movement-property/f tiled-map % position)))
      "none"))
