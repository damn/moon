(ns tiled.create-static-tiled-map-tile
  (:require [clojure.gdx :as gdx]))

(defn f
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (gdx/static-tiled-map-tile texture-region)]
    (gdx/map-properties-put! (gdx/tile-get-properties tile) property-name property-value)
    tile))
