(ns gdx.map-layers
  (:refer-clojure :exclude [get])
  (:require [com.badlogic.gdx.maps.map-layers :as map-layers]))

(defn add! [layers layer]
  (map-layers/add layers layer))

(defn get [layers name]
  (map-layers/get layers name))
