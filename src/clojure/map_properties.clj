(ns clojure.map-properties
  (:refer-clojure :exclude [get])
  (:require [com.badlogic.gdx.maps.map-properties :as map-properties]))

(defn get [& args]
  (apply map-properties/get args))

(defn get-keys [& args]
  (apply map-properties/get-keys args))

(defn get-values [& args]
  (apply map-properties/get-values args))

(defn put! [& args]
  (apply map-properties/put! args))

(defn clojurize [& args]
  (apply map-properties/clojurize args))
