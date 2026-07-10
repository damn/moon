(ns clojure.movement-property-layers
  (:require [gdl.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [gdl.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [gdl.maps.tiled.tiled-map :as tiled-map]
            [gdl.maps.map-properties :as map-properties]))

(defn f
  [tiled-map]
  (->> tiled-map
       tiled-map/get-layers
       reverse
       (filter #(map-properties/get (tiled-map-tile-layer/get-properties %) "movement-properties"))))
