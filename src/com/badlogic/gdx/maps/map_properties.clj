(ns com.badlogic.gdx.maps.map-properties
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn get [map-properties k]
  (MapProperties/.get map-properties k))

(defn get-keys [^MapProperties props]
  (.getKeys props))

(defn get-values [^MapProperties props]
  (.getValues props))

(defn put! [map-properties k v]
  (MapProperties/.put map-properties k v))

(defn clojurize [props]
  (zipmap (get-keys props)
          (get-values props)))
