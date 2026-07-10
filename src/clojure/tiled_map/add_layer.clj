(ns clojure.tiled-map.add-layer
  (:require [gdl.maps.tiled.tiled-map :as tiled-map]
            [gdl.maps.map-layers :as map-layers]
            [clojure.tiled-map.create-layer :as create-layer]))

(defn f [tiled-map layer]
  (map-layers/add! (tiled-map/get-layers tiled-map)
                   (create-layer/f tiled-map layer)))
