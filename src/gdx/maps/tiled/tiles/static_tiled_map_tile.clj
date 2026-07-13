(ns gdx.maps.tiled.tiles.static-tiled-map-tile
  (:require [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]))

(defn create [texture-region-or-tile]
  (static-tiled-map-tile/new texture-region-or-tile))

(defn get-properties [tile]
  (static-tiled-map-tile/getProperties tile))
