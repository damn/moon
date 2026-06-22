(ns gdl.movement-property-layers
  (:require [gdl.get-layers :refer [get-layers]]
            [gdl.get :refer [props-get]]
            [gdl.get-properties :refer [get-properties]]))

(defn f
  [tiled-map]
  (->> tiled-map
       get-layers
       reverse
       (filter #(props-get (get-properties %) "movement-properties"))))
