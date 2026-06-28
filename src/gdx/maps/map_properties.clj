(ns gdx.maps.map-properties
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn clojurize [^MapProperties props]
  (zipmap (.getKeys props)
          (.getValues props)))
