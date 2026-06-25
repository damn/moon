(ns tiled-map.add-layer
  (:require [map-layers.add :as add!]
            [tiled-map.get-layers :refer [get-layers]]
            [tiled-map.create-layer :as create-layer]))

(defn f [tiled-map layer]
  (add!/f (get-layers tiled-map)
          (create-layer/f tiled-map layer)))
