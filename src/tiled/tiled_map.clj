(ns tiled.tiled-map
  (:require [tiled-map.add-layer :as add-layer]
            [clojure.gdx.map-properties.put! :as put!]
            [clojure.gdx.tiled-map.get-properties :as get-properties]
            [clojure.gdx.tiled-map.new :as new-tiled-map]))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (new-tiled-map/f)]
    (doseq [[k v] properties]
      (assert (string? k))
      (put!/f (get-properties/f tiled-map) k v))
    (doseq [layer layers]
      (add-layer/f tiled-map layer))
    tiled-map))
