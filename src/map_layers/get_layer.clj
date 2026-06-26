(ns map-layers.get-layer
  (:require [com.badlogic.gdx.maps.map-layers :as map-layers]))

(defn get-layer [layers layer-name]
  (map-layers/get layers layer-name))
