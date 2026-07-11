(ns moon.movement-property
  (:require [moon.movement-property-layers :as movement-property-layers]
            [moon.tiled-map :as tiled-map]))

(defn f [tiled-map position]
  (or (->> tiled-map
           movement-property-layers/f
           (some #(tiled-map/tile-movement-property tiled-map % position)))
      "none"))
