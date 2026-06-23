(ns tiled.create-static-tiled-map-tile
  (:require [map-properties.put :as put!]
            [tiled-map-tile.get-properties :as get-properties]
            [tiled.static-tiled-map-tile :as static-tiled-map-tile]))

(defn f
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (static-tiled-map-tile/create texture-region)]
    (put!/f (get-properties/f tile) property-name property-value)
    tile))
