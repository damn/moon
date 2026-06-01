(ns clojure.gdx.maps.map-properties
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn get [^MapProperties props k]
  (.get props k))

(defn put! [^MapProperties props k v]
  (.put props k v))

(defn ->clj [^MapProperties props]
  (zipmap (.getKeys props)
          (.getValues props)))
