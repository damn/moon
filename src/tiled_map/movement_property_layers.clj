(ns tiled-map.movement-property-layers
  (:require
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.map-properties :as map-properties]
            [clojure.gdx.tiled-map-tile-layer.get-properties :as get-layer-properties]))

(defn f
  [tiled-map]
  (->> tiled-map
       tiled-map/get-layers
       reverse
       (filter #(map-properties/get (get-layer-properties/f %) "movement-properties"))))
