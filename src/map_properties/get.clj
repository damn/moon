(ns map-properties.get
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn f [^MapProperties props k]
  (.get props k))
