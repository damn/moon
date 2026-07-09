(ns com.badlogic.gdx.maps.map-layers
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapLayers)))

(defn add [^MapLayers map-layers layer]
  (.add map-layers layer))

(defn get [map-layers layer-name]
  (MapLayers/.get map-layers ^String layer-name))
