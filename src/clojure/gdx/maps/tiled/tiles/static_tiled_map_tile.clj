(ns clojure.gdx.maps.tiled.tiles.static-tiled-map-tile
  (:require [clojure.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as tile]))

(def copy (memoize tile/copy))

(defn create
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (tile/create texture-region)]
    (props/put! (tile/properties tile) property-name property-value)
    tile))
