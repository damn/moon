(ns com.badlogic.gdx.maps.map-layers
  (:require [gdl.maps.map-layers :as layers])
  (:import (com.badlogic.gdx.maps MapLayer
                                  MapLayers)))

(extend-type MapLayers
  layers/Layers
  (add! [layers layer]
    (.add layers layer))

  (get [layers ^String layer-name]
    (.get layers layer-name))

  (get-index [layers ^MapLayer layer]
    (.getIndex layers layer)))
