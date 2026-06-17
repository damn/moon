(ns com.badlogic.gdx.maps.properties.get
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn props-get [^MapProperties props k]
  (.get props k))
