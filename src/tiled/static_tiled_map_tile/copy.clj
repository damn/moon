(ns tiled.static-tiled-map-tile.copy
  (:require [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]))

(defn f [tile]
  (static-tiled-map-tile/copy tile))
