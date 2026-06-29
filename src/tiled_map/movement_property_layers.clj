(ns tiled-map.movement-property-layers
  (:require [tiled-map.get-layers :refer [get-layers]])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn f
  [tiled-map]
  (->> tiled-map
       get-layers
       reverse
       (filter #(MapProperties/.get (.getProperties %) "movement-properties"))))
