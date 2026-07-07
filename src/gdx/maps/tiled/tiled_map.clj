(ns gdx.maps.tiled.tiled-map
  (:require [clojure.tiled-map :as tiled-map]
            [clojure.map-properties :as map-properties]
            [gdx.maps.tiled.add-layer :as add-layer]))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (tiled-map/new)]
    (doseq [[k v] properties]
      (assert (string? k))
      (map-properties/put! (tiled-map/get-properties tiled-map) k v))
    (doseq [layer layers]
      (add-layer/f tiled-map layer))
    tiled-map))
