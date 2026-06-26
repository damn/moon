(ns tiled-map-tile-layer.get-cell
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]))

(defn get-cell [layer [x y]]
  (layer/cell layer x y))
