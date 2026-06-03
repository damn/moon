(ns clojure.gdx.maps.map-properties
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn put! [^MapProperties props k v]
  (.put props k v))

(defn ->clj [^MapProperties props]
  (zipmap (.getKeys props)
          (.getValues props)))
