(ns gdl.create-static-tiled-map-tile
  (:require [gdl.put :refer [put!]]
            [gdl.get-properties :refer [get-properties]]
            [gdl.static-tiled-map-tile :as static-tiled-map-tile]))

(defn f
  [texture-region property-name property-value]
  {:pre [texture-region
         (string? property-name)]}
  (let [tile (static-tiled-map-tile/create texture-region)]
    (put! (get-properties tile) property-name property-value)
    tile))
