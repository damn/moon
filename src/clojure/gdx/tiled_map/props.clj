(ns clojure.gdx.tiled-map.props
  (:refer-clojure :exclude [get])
  (:require [clj.api.com.badlogic.gdx.maps.map-properties :as props]))

(defn get [props key]
  (props/get props key))

(defn ->clj [props]
  (zipmap (props/keys   props)
          (props/values props)))

(defn add! [props m]
  (doseq [[k v] m]
    (assert (string? k))
    (props/put! props k v)))

(def put-all! props/put-all!)

(def create props/create)

(def put! props/put!)
