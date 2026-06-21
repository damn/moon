(ns clojure.map-layers.add
  (:import (com.badlogic.gdx.maps MapLayer
                                  MapLayers)))

(defn add! [^MapLayers layers ^MapLayer layer]
  (.add layers layer))
