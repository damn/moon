(ns tiled-map.movement-property
  (:require [tiled-map.movement-property-layers :as movement-property-layers]
            [tiled-map.tile-movement-property :as tile-movement-property]))

(defn f [tiled-map position]
  (or (->> tiled-map
           movement-property-layers/f
           (some #(tile-movement-property/f tiled-map % position)))
      "none"))
