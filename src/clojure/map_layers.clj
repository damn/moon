(ns clojure.map-layers
  (:refer-clojure :exclude [get add])
  (:import (com.badlogic.gdx.maps MapLayers)))

(defn add! [^MapLayers map-layers layer]
  (.add map-layers layer))

(defn get [map-layers layer-name]
  (MapLayers/.get map-layers ^String layer-name))
