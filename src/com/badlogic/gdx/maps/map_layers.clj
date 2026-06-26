(ns com.badlogic.gdx.maps.map-layers
  (:import (com.badlogic.gdx.maps MapLayers))
  (:refer-clojure :exclude [get]))

(defn get [^MapLayers layers ^String layer-name]
  (.get layers layer-name))

(defn add! [^MapLayers layers layer]
  (.add layers layer))
