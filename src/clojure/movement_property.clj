(ns clojure.movement-property
  (:require [clojure.movement-property-layers :as movement-property-layers]
            [clojure.tile-movement-property :as tile-movement-property]))

; moon.tiled-map

(defn f [tiled-map position]
  (or (->> tiled-map
           movement-property-layers/f
           (some #(tile-movement-property/f tiled-map % position)))
      "none"))
