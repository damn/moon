(ns tiled.tiled-map
  (:require
            [com.badlogic.gdx.maps.map-properties :as map-properties] [tiled-map.add-layer :as add-layer]
            [clojure.gdx.tiled-map.get-properties :as get-properties]
            [clojure.gdx.tiled-map.new :as new-tiled-map]))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (new-tiled-map/f)]
    (doseq [[k v] properties]
      (assert (string? k))
      (map-properties/put! (get-properties/f tiled-map) k v))
    (doseq [layer layers]
      (add-layer/f tiled-map layer))
    tiled-map))
