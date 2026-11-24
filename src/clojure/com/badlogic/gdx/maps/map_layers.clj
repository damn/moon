(ns clojure.com.badlogic.gdx.maps.map-layers
  "Ordered list of map layer instances owned by a map"
  (:import (com.badlogic.gdx.maps MapLayer MapLayers))
  (:refer-clojure :exclude [get]))

(defn get
  "Returns the MapLayer at the specified index"
  [^MapLayers map-layers ^String layer-name]
  (.get map-layers layer-name))

(defn add!
  "- `layer`: layer to be added to the set"
  [^MapLayers map-layers ^MapLayer layer]
  (.add map-layers layer))
