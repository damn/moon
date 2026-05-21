(ns com.badlogic.gdx.maps.map-properties
  (:require [gdl.maps.map-properties :as props])
  (:import (com.badlogic.gdx.maps MapProperties)))

(extend-type MapProperties
  props/Props
  (get [map-properties k]
    (.get map-properties k))

  (add! [props m]
    (doseq [[k v] m]
      (assert (string? k))
      (.put props k v)))

  (->clj [props]
    (zipmap (.getKeys props)
            (.getValues props))))
