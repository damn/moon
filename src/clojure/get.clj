(ns clojure.get
  (:refer-clojure :exclude [get])
  (:import (com.badlogic.gdx.maps MapLayers)))

(defn f [map-layers layer-name]
  (MapLayers/.get map-layers ^String layer-name))
