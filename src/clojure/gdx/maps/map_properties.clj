(ns clojure.gdx.maps.map-properties
  (:refer-clojure :exclude [get])
  (:require [com.badlogic.gdx.maps.map-properties :as props]))

(def get props/get)

(defn add! [props m]
  (doseq [[k v] m]
    (assert (string? k))
    (props/put! props k v)))

(defn ->clj [props]
  (zipmap (props/keys props)
          (props/vals props)))
