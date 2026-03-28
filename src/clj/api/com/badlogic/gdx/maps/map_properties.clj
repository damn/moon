(ns clj.api.com.badlogic.gdx.maps.map-properties
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn get [^MapProperties properties key]
  (.get properties key))
