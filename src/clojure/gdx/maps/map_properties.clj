(ns clojure.gdx.maps.map-properties
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn get [^MapProperties props k]
  (.get props k))

(defn add! [^MapProperties map-properties m]
  (doseq [[k v] m]
    (assert (string? k))
    (.put map-properties k v)))

(defn create [m]
  (doto (MapProperties.)
    (add! m)))

(defn ->clj [^MapProperties map-properties]
  (zipmap (.getKeys   map-properties)
          (.getValues map-properties)))

(defn put-all! [^MapProperties map-properties other-properties]
  (.putAll map-properties other-properties))
