(ns map-layers.add
  (:import (com.badlogic.gdx.maps MapLayer
                                  MapLayers)))

(defn f [^MapLayers layers ^MapLayer layer]
  (.add layers layer))
