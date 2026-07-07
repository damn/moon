(ns clojure.add!
  (:import (com.badlogic.gdx.maps MapLayers)))

(defn f [^MapLayers map-layers layer]
  (.add map-layers layer))
