(ns clojure.tiled-tiled-map
  (:require [gdl.tiled-map :as tiled-map]
            [gdl.map-properties :as map-properties]
            [clojure.tiled-map.add-layer :as add-layer]))

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
