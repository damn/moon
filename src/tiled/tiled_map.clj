(ns tiled.tiled-map
  (:require [map-properties.put :as put!]
            [tiled-map.get-properties :as get-properties]
            [tiled-map.add-layer :as add-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (tiled-map/create)]
    (doseq [[k v] properties]
      (assert (string? k))
      (put!/f (get-properties/f tiled-map) k v))
    (doseq [layer layers]
      (add-layer/f tiled-map layer))
    tiled-map))
