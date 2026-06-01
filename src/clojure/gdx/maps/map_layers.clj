(ns clojure.gdx.maps.map-layers
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapLayer
                                  MapLayers)))

(defn get [^MapLayers layers ^String layer-name]
  (.get layers layer-name))

(defn add! [^MapLayers layers ^MapLayer layer]
  (.add layers layer))
