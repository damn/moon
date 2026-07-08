(ns clojure.tiled-map.add-layer
  (:require [clojure.tiled-map :as tiled-map]
            [clojure.map-layers.add! :as add!]
            [clojure.create-layer :as create-layer]))

(defn f [tiled-map layer]
  (add!/f (tiled-map/get-layers tiled-map)
         (create-layer/f tiled-map layer)))
