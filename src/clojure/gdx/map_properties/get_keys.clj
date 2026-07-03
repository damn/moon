(ns clojure.gdx.map-properties.get-keys
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn f [^MapProperties props]
  (.getKeys props))
