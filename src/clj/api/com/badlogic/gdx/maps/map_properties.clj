(ns clj.api.com.badlogic.gdx.maps.map-properties
  (:refer-clojure :exclude [get keys])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn create []
  (MapProperties.))

(defn get [^MapProperties properties key]
  (.get properties key))

(defn keys [^MapProperties properties]
  (.getKeys properties))

(defn values [^MapProperties properties]
  (.getValues properties))

(defn put! [^MapProperties properties k v]
  (.put properties k v))

(defn put-all! [^MapProperties properties map-properties]
  (.putAll properties map-properties))
