(ns clojure.property-value
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer$cell :as tiled-map-tile-layer-cell]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.map-properties :as map-properties]))

(defn property-value [layer [x y] property-key]
  (if-let [cell (tiled-map-tile-layer/getCell layer x y)]
    (if-let [value (map-properties/get (tiled-map-tile/getProperties (tiled-map-tile-layer-cell/getTile cell)) property-key)]
      value
      :undefined)
    :no-cell))
