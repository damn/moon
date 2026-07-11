(ns moon.tiled-map.create
  (:require [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [moon.map-properties :as map-properties]
            [moon.tiled-map :as moon-tiled-map]))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (tiled-map/new)]
    (doseq [[k v] properties]
      (assert (string? k))
      (map-properties/put! (tiled-map/getProperties tiled-map) k v))
    (doseq [layer layers]
      (moon-tiled-map/add-layer! tiled-map layer))
    tiled-map))
