(ns tiled-map-tile-layer.get-width
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]))

(defn get-width [layer]
  (layer/width layer))
