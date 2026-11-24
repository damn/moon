(ns clojure.com.badlogic.gdx.maps.map-properties
  "@brief Set of string indexed values representing map elements' properties, allowing to retrieve, modify and add properties to
   the set."
  (:import (com.badlogic.gdx.maps MapProperties))
  (:refer-clojure :exclude [get]))

(defn put! [^MapProperties map-properties k v]
  (.put map-properties k v))

(defn get
  "Returns the value for that property if it exists, otherwise, null"
  [^MapProperties map-properties k]
  (.get map-properties k))

(defn get-keys
  "Returns iterator for the property names"
  [map-properties]
  (MapProperties/.getKeys map-properties))

(defn get-values
  "Returns iterator to properties' values"
  [map-properties]
  (MapProperties/.getValues map-properties))
