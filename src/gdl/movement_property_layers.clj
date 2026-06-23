(ns gdl.movement-property-layers
  (:require [tiled-map.get-layers :refer [get-layers]]
            [map-properties.get :as get]
            [tiled-map-tile-layer.get-properties :as get-properties]))

(defn f
  [tiled-map]
  (->> tiled-map
       get-layers
       reverse
       (filter #(get/f (get-properties/f %) "movement-properties"))))
