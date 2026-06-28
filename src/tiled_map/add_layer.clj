(ns tiled-map.add-layer
  (:require [tiled-map.get-layers :refer [get-layers]]
            [tiled-map.create-layer :as create-layer])
  (:import (com.badlogic.gdx.maps MapLayers)))

(defn f [tiled-map layer]
  (MapLayers/.add (get-layers tiled-map)
                  (create-layer/f tiled-map layer)))
