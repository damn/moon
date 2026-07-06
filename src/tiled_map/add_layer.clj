(ns tiled-map.add-layer
  (:require [com.badlogic.gdx.maps.map-layers :as map-layers]
            [clojure.gdx.tiled-map.get-layers :as get-layers]
            [tiled-map.create-layer :as create-layer]))

(defn f [tiled-map layer]
  (map-layers/add (get-layers/f tiled-map)
         (create-layer/f tiled-map layer)))
