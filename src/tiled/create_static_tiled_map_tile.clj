(ns tiled.create-static-tiled-map-tile
  (:require [clojure.gdx.map-properties.put! :as put!]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]))

(defn f
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (static-tiled-map-tile/new texture-region)]
    (put!/f (static-tiled-map-tile/get-properties tile) property-name property-value)
    tile))
