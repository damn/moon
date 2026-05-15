(ns com.badlogic.gdx.maps.map-properties
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn get [^MapProperties map-properties k]
  (.get map-properties k))

(defn add! [^MapProperties props m]
  (doseq [[k v] m]
    (assert (string? k))
    (.put props k v)))

(defn create [properties]
  (doto (MapProperties.)
    (add! properties)))

(defn ->clj [^MapProperties props]
  (zipmap (.getKeys   props)
          (.getValues props)))
