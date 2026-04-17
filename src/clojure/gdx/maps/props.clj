(ns clojure.gdx.maps.props
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn create []
  (MapProperties.))

(defn put! [^MapProperties props k v]
  (.put props k v))

(defn put-all! [^MapProperties props other-props]
  (.putAll props other-props))

(defn get [^MapProperties props key]
  (.get props key))

(defn ->clj [^MapProperties props]
  (zipmap (.getKeys   props)
          (.getValues props)))

(defn add! [props m]
  (doseq [[k v] m]
    (assert (string? k))
    (put! props k v)))
