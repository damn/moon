(ns clojure.gdx.maps.tiled.tiled-map.create
  (:require [clojure.put :refer [put!]]
            [clojure.gdx.maps.tiled.tiled-map.get-properties :refer [get-properties]]
            [clojure.gdx.maps.tiled.tiled-map.add-layer :as add-layer])
  (:import (com.badlogic.gdx.maps.tiled TiledMap)))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (TiledMap.)]
    (doseq [[k v] properties]
      (assert (string? k))
      (put! (get-properties tiled-map) k v))
    (doseq [layer layers]
      (add-layer/f tiled-map layer))
    tiled-map))
