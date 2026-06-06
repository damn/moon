(ns gdx.tiled-map.movement-property-layers
  (:require [gdx.maps.tiled.tiled-map.get-layers :refer [get-layers]]
            [gdx.maps.properties.get :refer [props-get]]
            [clojure.get-properties :refer [get-properties]]))

(defn f
  [tiled-map]
  (->> tiled-map
       get-layers
       reverse
       (filter #(props-get (get-properties %) "movement-properties"))))
