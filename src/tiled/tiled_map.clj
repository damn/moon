(ns tiled.tiled-map
  (:require [clojure.gdx :as gdx])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (TiledMap.)]
    (doseq [[k v] properties]
      (assert (string? k))
      (.put (.getProperties tiled-map) k v))
    (doseq [layer layers]
      (gdx/add-layer! tiled-map layer))
    tiled-map))
