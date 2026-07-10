(ns gdl.maps.map-properties
  (:refer-clojure :exclude [get])
  (:require [com.badlogic.gdx.maps.map-properties :as map-properties]))

(defn get [map-properties k]
  (map-properties/get map-properties k))

(defn put! [map-properties k v]
  (map-properties/put map-properties k v))

(defn clojurize [map-properties]
  (zipmap (map-properties/getKeys   map-properties)
          (map-properties/getValues map-properties)))
