(ns tiled-map-tile-layer.get-height
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]))

(defn get-height [layer]
  (layer/height layer))
