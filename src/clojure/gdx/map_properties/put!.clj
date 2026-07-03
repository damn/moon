(ns clojure.gdx.map-properties.put!
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn f [map-properties k v]
  (MapProperties/.put map-properties k v))
