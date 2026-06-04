(ns clojure.gdx.maps.tiled.tiled-map.create
  (:require [clojure.gdx.maps.map-properties :as props]
            [clojure.gdx.maps.tiled.tiled-map :as tiled-map]
            [clojure.gdx.maps.tiled.tiled-map.get-properties :refer [get-properties]]
            [clojure.gdx.maps.tiled.tiled-map.add-layer :as add-layer]))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (tiled-map/create)]
    (doseq [[k v] properties]
      (assert (string? k))
      (props/put! (get-properties tiled-map) k v))
    (doseq [layer layers]
      (add-layer/f tiled-map layer))
    tiled-map))
