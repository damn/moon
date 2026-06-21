(ns clojure.maps.layers.get-layer
  (:import (com.badlogic.gdx.maps MapLayers)))

(defn get-layer [^MapLayers layers ^String layer-name]
  (.get layers layer-name))
