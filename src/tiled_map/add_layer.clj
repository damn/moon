(ns tiled-map.add-layer
  (:require [clojure.gdx.map-layers.add :as add]
            [clojure.gdx.tiled-map.get-layers :as get-layers]
            [tiled-map.create-layer :as create-layer]))

(defn f [tiled-map layer]
  (add/f (get-layers/f tiled-map)
         (create-layer/f tiled-map layer)))
