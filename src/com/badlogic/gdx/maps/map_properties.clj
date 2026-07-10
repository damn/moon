(ns com.badlogic.gdx.maps.map-properties
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn get [map-properties k]
  (.get ^MapProperties map-properties k))

(defn getKeys [map-properties]
  (.getKeys ^MapProperties map-properties))

(defn getValues [map-properties]
  (.getValues ^MapProperties map-properties))

(defn put [map-properties k v]
  (.put ^MapProperties map-properties k v))
