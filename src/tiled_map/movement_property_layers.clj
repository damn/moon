(ns tiled-map.movement-property-layers
  (:require [tiled-map.get-layers :refer [get-layers]]
            [tiled-map-tile-layer.get-properties :as get-properties])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn f
  [tiled-map]
  (->> tiled-map
       get-layers
       reverse
       (filter #(MapProperties/.get (get-properties/f %) "movement-properties"))))
