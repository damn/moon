(ns gdx.maps.map-properties
  (:refer-clojure :exclude [get])
  (:require [com.badlogic.gdx.maps.map-properties :as map-properties]))

(defn get [properties k]
  (map-properties/get properties k))

(defn put! [properties k v]
  (map-properties/put properties k v))

(defn clojurize [properties]
  (zipmap (map-properties/getKeys properties)
          (map-properties/getValues properties)))
