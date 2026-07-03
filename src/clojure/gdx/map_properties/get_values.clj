(ns clojure.gdx.map-properties.get-values
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn f [^MapProperties props]
  (.getValues props))
