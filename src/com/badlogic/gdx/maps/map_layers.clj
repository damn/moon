(ns com.badlogic.gdx.maps.map-layers
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapLayers)))

(defn add [map-layers layer]
  (.add ^MapLayers map-layers layer))

(defn get [map-layers layer-name]
  (.get ^MapLayers map-layers ^String layer-name))
