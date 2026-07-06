(ns tiled-map.add-layer
  (:require [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.map-layers :as map-layers]
            [tiled-map.create-layer :as create-layer]))

(defn f [tiled-map layer]
  (map-layers/add! (tiled-map/get-layers tiled-map)
         (create-layer/f tiled-map layer)))
