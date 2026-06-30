(ns tiled-map.movement-property-layers
  (:require [clojure.gdx :as gdx]))

(defn f
  [tiled-map]
  (->> (gdx/tiled-map-get-layers tiled-map)
       reverse
       (filter #(gdx/map-properties-get (gdx/tiled-map-tile-layer-get-properties %)
                                       "movement-properties"))))
