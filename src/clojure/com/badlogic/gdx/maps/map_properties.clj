(ns clojure.com.badlogic.gdx.maps.map-properties
  (:import (com.badlogic.gdx.maps MapProperties))
  (:refer-clojure :exclude [get]))

(defn put! [^MapProperties map-properties k v]
  (.put map-properties k v))

(defn get
  [^MapProperties map-properties k]
  (.get map-properties k))

(defn get-keys [map-properties]
  (MapProperties/.getKeys map-properties))

(defn get-values [map-properties]
  (MapProperties/.getValues map-properties))
