(ns clojure.tiled-map.add-layer
  (:require [clojure.tiled-map :as tiled-map]
            [gdl.map-layers :as map-layers]
            [clojure.tiled-map.create-layer :as create-layer]))

(defn f [tiled-map layer]
  (map-layers/add! (tiled-map/get-layers tiled-map)
                   (create-layer/f tiled-map layer)))
