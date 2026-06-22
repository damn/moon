(ns gdl.tiled-map.movement-property
  (:require [gdl.tiled-map.movement-property-layers :as movement-property-layers]
            [gdl.tiled-map.tile-movement-property :as tile-movement-property]))

(defn f [tiled-map position]
  (or (->> tiled-map
           movement-property-layers/f
           (some #(tile-movement-property/f tiled-map % position)))
      "none"))
