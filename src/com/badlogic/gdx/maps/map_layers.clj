(ns com.badlogic.gdx.maps.map-layers
  (:import (com.badlogic.gdx.maps MapLayers)))

(defn add! [^MapLayers layers layer]
  (.add layers layer))
