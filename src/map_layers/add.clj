(ns map-layers.add
  (:require [com.badlogic.gdx.maps.map-layers :as map-layers]
            [com.badlogic.gdx.maps.map-layer :as map-layer]))

(defn f [layers layer]
  (map-layers/add! layers (map-layer/type-hint layer)))
