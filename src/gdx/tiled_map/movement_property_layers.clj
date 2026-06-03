(ns gdx.tiled-map.movement-property-layers
  (:require [clojure.gdx.maps.tiled.tiled-map :as tiled-map]
            [clojure.gdx.maps.map-properties.get :refer [props-get]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer :as layer]))

(defn f
  [tiled-map]
  (->> tiled-map
       tiled-map/layers
       reverse
       (filter #(props-get (layer/properties %) "movement-properties"))))
