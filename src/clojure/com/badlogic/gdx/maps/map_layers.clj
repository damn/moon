(ns clojure.com.badlogic.gdx.maps.map-layers
  (:import (com.badlogic.gdx.maps MapLayer MapLayers))
  (:refer-clojure :exclude [get]))

(defn get [^MapLayers map-layers ^String layer-name]
  (.get map-layers layer-name))

(defn add! [^MapLayers map-layers ^MapLayer layer]
  (.add map-layers layer))
