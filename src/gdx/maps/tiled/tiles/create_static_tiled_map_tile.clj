(ns gdx.maps.tiled.tiles.create-static-tiled-map-tile
  (:require [clojure.map-properties :as map-properties]
            [clojure.static-tiled-map-tile :as static-tiled-map-tile]))

(defn f
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (static-tiled-map-tile/new texture-region)]
    (map-properties/put! (static-tiled-map-tile/get-properties tile) property-name property-value)
    tile))
