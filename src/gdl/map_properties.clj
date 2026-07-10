(ns gdl.map-properties
  (:refer-clojure :exclude [get])
  (:require [com.badlogic.gdx.maps.map-properties :as map-properties]))

(defn get [& args]
  (apply map-properties/get args))

(defn put! [& args]
  (apply map-properties/put args))

(defn clojurize [map-properties]
  (zipmap (map-properties/getKeys   map-properties)
          (map-properties/getValues map-properties)))
