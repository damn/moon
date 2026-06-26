(ns tiled-map-tile-layer.get-name
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]))

(defn get-name [layer]
  (layer/name layer))
