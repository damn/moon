(ns gdx.tiled-map.movement-property-layers
  (:require [com.badlogic.gdx.maps.tiled.tiled-map.get-layers :refer [get-layers]]
            [com.badlogic.gdx.maps.properties.get :refer [props-get]]
            [com.badlogic.gdx.maps.get-properties :refer [get-properties]]))

(defn f
  [tiled-map]
  (->> tiled-map
       get-layers
       reverse
       (filter #(props-get (get-properties %) "movement-properties"))))
