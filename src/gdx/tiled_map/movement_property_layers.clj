(ns gdx.tiled-map.movement-property-layers
  (:require [clojure.maps.tiled.tiled-map.get-layers :refer [get-layers]]
            [clojure.maps.map-properties.get :refer [props-get]]
            [clojure.get-properties :refer [get-properties]]))

(defn f
  [tiled-map]
  (->> tiled-map
       get-layers
       reverse
       (filter #(props-get (get-properties %) "movement-properties"))))
