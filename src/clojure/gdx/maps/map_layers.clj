(ns clojure.gdx.maps.map-layers
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapLayer
                                  MapLayers)))

(defn get [^MapLayers layers name]
  (.get layers ^String name))

(defn add! [^MapLayers layers layer]
  (.add layers layer))

(defn get-index [^MapLayers layers ^MapLayer layer]
  (.getIndex layers layer))
