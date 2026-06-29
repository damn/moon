(ns tiled.tiled-map
  (:require [tiled-map.add-layer :as add-layer])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (TiledMap.)]
    (doseq [[k v] properties]
      (assert (string? k))
      (.put (.getProperties tiled-map) k v))
    (doseq [layer layers]
      (add-layer/f tiled-map layer))
    tiled-map))
