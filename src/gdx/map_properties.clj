(ns gdx.map-properties
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn get [properties k]
  (.get ^MapProperties properties k))

(defn put! [properties k v]
  (.put ^MapProperties properties k v))

(defn clojurize [properties]
  (zipmap (.getKeys ^MapProperties properties)
          (.getValues ^MapProperties properties)))
