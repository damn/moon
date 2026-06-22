(ns gdl.tiled-map.movement-property-layers
  (:require [gdl.tiled-map.get-layers :refer [get-layers]]
            [gdl.map-properties.get :refer [props-get]]
            [gdl.get-properties :refer [get-properties]]))

(defn f
  [tiled-map]
  (->> tiled-map
       get-layers
       reverse
       (filter #(props-get (get-properties %) "movement-properties"))))
