(ns gdx.maps.tiled.movement-property-layers
  (:require [clojure.tiled-map-tile-layer :as tiled-map-tile-layer]
            [clojure.tiled-map-tile :as tiled-map-tile]
            [clojure.tiled-map :as tiled-map]
            [clojure.map-properties :as map-properties]))

(defn f
  [tiled-map]
  (->> tiled-map
       tiled-map/get-layers
       reverse
       (filter #(map-properties/get (tiled-map-tile-layer/get-properties %) "movement-properties"))))
