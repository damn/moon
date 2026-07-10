(ns clojure.tiled-tiled-map
  (:require [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [gdl.maps.map-properties :as map-properties]
            [clojure.tiled-map.add-layer :as add-layer]))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (tiled-map/new)]
    (doseq [[k v] properties]
      (assert (string? k))
      (map-properties/put! (tiled-map/getProperties tiled-map) k v))
    (doseq [layer layers]
      (add-layer/f tiled-map layer))
    tiled-map))
