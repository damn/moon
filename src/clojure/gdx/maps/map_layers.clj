(ns clojure.gdx.maps.map-layers
  (:require [clojure.maps.map-layers :as layers])
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
