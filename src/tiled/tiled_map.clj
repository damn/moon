(ns tiled.tiled-map
  (:require [tiled-map.add-layer :as add-layer]
            [clojure.gdx.map-properties.put! :as put!]
            [clojure.gdx.tiled-map.get-properties :as get-properties])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (TiledMap.)]
    (doseq [[k v] properties]
      (assert (string? k))
      (put!/f (get-properties/f tiled-map) k v))
    (doseq [layer layers]
      (add-layer/f tiled-map layer))
    tiled-map))
