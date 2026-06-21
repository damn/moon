(ns clojure.map-properties.put
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn put! [^MapProperties props k v]
  (.put props k v))
