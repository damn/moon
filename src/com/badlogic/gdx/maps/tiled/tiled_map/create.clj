(ns com.badlogic.gdx.maps.tiled.tiled-map.create
  (:require [com.badlogic.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiled-map.add-layer :as add-layer]))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (tiled-map/create)]
    (doseq [[k v] properties]
      (assert (string? k))
      (props/put! (tiled-map/props tiled-map) k v))
    (doseq [layer layers]
      (add-layer/f tiled-map layer))
    tiled-map))
