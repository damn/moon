(ns tiled-map-tile-layer.get-properties
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]))

(defn f [layer]
  (layer/properties layer))
