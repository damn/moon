(ns map-properties.clojurize
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn f [^MapProperties props]
  (zipmap (.getKeys props)
          (.getValues props)))
