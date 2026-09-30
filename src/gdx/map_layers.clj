(ns gdx.map-layers
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapLayer MapLayers)))

(defn add! [layers layer]
  (.add ^MapLayers layers ^MapLayer layer))

(defn get [layers name]
  (.get ^MapLayers layers ^String name))
