(ns tiled-map-tile.get-properties
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tile]))

(defn f [tile]
  (tile/properties tile))
