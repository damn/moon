(ns com.badlogic.gdx.maps.map-layers
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapLayers)))

(defn add! [^MapLayers layers layer]
  (.add layers layer))

(defn get [^MapLayers layers ^String layer-name]
  (.get layers layer-name))

(defn get-index [^MapLayers layers layer]
  (.getIndex layers layer))
