(ns gdl.map-layers
  (:refer-clojure :exclude [get])
  (:require [com.badlogic.gdx.maps.map-layers :as map-layers]))

(defn add! [& args]
  (apply map-layers/add args))

(defn get [& args]
  (apply map-layers/get args))
