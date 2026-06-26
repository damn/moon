(ns tiled-map-tile-layer.visible
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]))

(defn visible? [layer]
  (layer/visible? layer))
