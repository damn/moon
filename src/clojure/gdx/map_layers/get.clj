(ns clojure.gdx.map-layers.get
  (:import (com.badlogic.gdx.maps MapLayers)))

(defn f [map-layers layer-name]
  (MapLayers/.get map-layers layer-name))
