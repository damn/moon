(ns com.badlogic.gdx.maps.tiled
  (:require [com.badlogic.gdx.maps.map-layers :as layers])
  (:import (com.badlogic.gdx.maps MapLayer
                                  MapLayers)
           (com.badlogic.gdx.maps.tiled TmxMapLoader)))

(defn load! [tmx-file]
  (.load (TmxMapLoader.) tmx-file))

(extend-type MapLayers
  layers/Layers
  (add! [layers layer]
    (.add layers layer))

  (get [layers ^String layer-name]
    (.get layers layer-name))

  (get-index [layers ^MapLayer layer]
    (.getIndex layers layer)))
