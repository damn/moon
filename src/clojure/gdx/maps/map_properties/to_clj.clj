(ns clojure.gdx.maps.map-properties.to-clj
  (:import (com.badlogic.gdx.maps MapProperties)))

(defn ->clj [^MapProperties props]
  (zipmap (.getKeys props)
          (.getValues props)))
