(ns clojure.maps.tiled.tiles.static-tiled-map-tile.create
  (:require [clojure.put :refer [put!]]
            [clojure.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]))

(defn f
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (static-tiled-map-tile/create texture-region)]
    (put! (static-tiled-map-tile/properties tile) property-name property-value)
    tile))
