(ns clojure.movement-property-layers
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.map-properties :as map-properties]))

(defn f
  [tiled-map]
  (->> tiled-map
       tiled-map/getLayers
       reverse
       (filter #(map-properties/get (tiled-map-tile-layer/getProperties %) "movement-properties"))))
