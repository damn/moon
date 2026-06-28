(ns map-properties.put
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn f [^MapProperties props k v]
  (.put props k v))
