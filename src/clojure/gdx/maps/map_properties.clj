(ns clojure.gdx.maps.map-properties
  (:refer-clojure :exclude [get])
  (:require [com.badlogic.gdx.maps.map-properties :as props]))

(def put-all! props/put-all!)

(defn add! [props m]
  (doseq [[k v] m]
    (assert (string? k))
    (props/put! props k v)))

(defn create [properties]
  (doto (props/create)
    (add! properties)))

(def get props/get)

(defn ->clj [props]
  (zipmap (props/keys props)
          (props/vals props)))
