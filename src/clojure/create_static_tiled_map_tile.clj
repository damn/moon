(ns clojure.create-static-tiled-map-tile
  (:require [clojure.map-properties.put :refer [put!]]
            [clojure.get-properties :refer [get-properties]]
            [clojure.static-tiled-map-tile :as static-tiled-map-tile]))

(defn f
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (static-tiled-map-tile/create texture-region)]
    (put! (get-properties tile) property-name property-value)
    tile))
