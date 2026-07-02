(ns clojure.gdx.map-properties.get
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn f [map-properties k]
  (MapProperties/.get map-properties k))
