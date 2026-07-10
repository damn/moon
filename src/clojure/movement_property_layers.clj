(ns clojure.movement-property-layers
  (:require [gdl.tiled-map-tile-layer :as tiled-map-tile-layer]
            [gdl.tiled-map-tile :as tiled-map-tile]
            [gdl.tiled-map :as tiled-map]
            [gdl.map-properties :as map-properties]))

(defn f
  [tiled-map]
  (->> tiled-map
       tiled-map/get-layers
       reverse
       (filter #(map-properties/get (tiled-map-tile-layer/get-properties %) "movement-properties"))))
