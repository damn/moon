(ns com.badlogic.gdx.maps.map-layers
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapLayer MapLayers)))

(defn add [map-layers layer]
  (.add ^MapLayers map-layers ^MapLayer layer))

(defn get [map-layers layer-name]
  (.get ^MapLayers map-layers ^String layer-name))
