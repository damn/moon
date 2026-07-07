(ns clojure.add-layer
  (:require [clojure.tiled-map :as tiled-map]
            [clojure.map-layers :as map-layers]
            [clojure.create-layer :as create-layer]))

(defn f [tiled-map layer]
  (map-layers/add! (tiled-map/get-layers tiled-map)
         (create-layer/f tiled-map layer)))
