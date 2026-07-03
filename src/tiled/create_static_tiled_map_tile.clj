(ns tiled.create-static-tiled-map-tile
  (:require [clojure.gdx.map-properties.put! :as put!]
            [clojure.gdx.static-tiled-map-tile.get-properties :as get-properties]
            [clojure.gdx.static-tiled-map-tile.new :as new-static-tiled-map-tile]))

(defn f
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (new-static-tiled-map-tile/f texture-region)]
    (put!/f (get-properties/f tile) property-name property-value)
    tile))
