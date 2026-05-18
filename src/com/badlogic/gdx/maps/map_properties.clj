(ns com.badlogic.gdx.maps.map-properties
  (:refer-clojure :exclude [get keys vals])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn get [^MapProperties map-properties k]
  (.get map-properties k))

(defn put! [^MapProperties props k v]
  (.put props k v))

(defn keys [^MapProperties props]
  (.getKeys props))

(defn vals [^MapProperties props]
  (.getValues props))
