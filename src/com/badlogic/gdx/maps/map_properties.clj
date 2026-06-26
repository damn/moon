(ns com.badlogic.gdx.maps.map-properties
  (:import (com.badlogic.gdx.maps MapProperties))
  (:refer-clojure :exclude [get keys]))

(defn get [^MapProperties props k]
  (.get props k))

(defn put! [^MapProperties props k v]
  (.put props k v))

(defn keys [^MapProperties props]
  (.getKeys props))

(defn values [^MapProperties props]
  (.getValues props))
