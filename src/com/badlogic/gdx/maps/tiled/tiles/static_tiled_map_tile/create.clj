(ns com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile.create
  (:require [com.badlogic.gdx.maps.properties.put :refer [put!]]
            [com.badlogic.gdx.maps.get-properties :refer [get-properties]]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]))

(defn f
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (static-tiled-map-tile/create texture-region)]
    (put! (get-properties tile) property-name property-value)
    tile))
