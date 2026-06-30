(ns tiled.tiled-map
  (:require [clojure.gdx :as gdx]))

(defn f
  [{:keys [properties
           layers]}]
  (let [tiled-map (gdx/tiled-map)]
    (doseq [[k v] properties]
      (assert (string? k))
      (gdx/map-properties-put! (gdx/tiled-map-get-properties tiled-map) k v))
    (doseq [layer layers]
      (gdx/add-layer! tiled-map layer))
    tiled-map))
