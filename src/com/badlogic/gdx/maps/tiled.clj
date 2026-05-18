(ns com.badlogic.gdx.maps.tiled
  (:require [clojure.tiled-map.layers :as layers]
            [clojure.tiled-map.props :as props])
  (:import (com.badlogic.gdx.maps MapLayer
                                  MapLayers
                                  MapProperties)
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

(extend-type MapProperties
  props/Props
  (get [map-properties k]
    (.get map-properties k))

  (add! [props m]
    (doseq [[k v] m]
      (assert (string? k))
      (.put props k v)))

  (->clj [props]
    (zipmap (.getKeys props)
            (.getValues props))))
