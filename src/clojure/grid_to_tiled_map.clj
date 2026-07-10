(ns clojure.grid-to-tiled-map
  (:require [gdl.tiled-map-tile-layer-cell :as tiled-map-tile-layer-cell]
            [gdl.tiled-map-tile-layer :as tiled-map-tile-layer]
            [gdl.tiled-map-tile :as tiled-map-tile]
            [gdl.tiled-map :as tiled-map]
            [gdl.static-tiled-map-tile :as static-tiled-map-tile]
            [clojure.g2d.posis :as posis]
            [clojure.g2d.width :refer [->width]]
            [clojure.g2d.height :refer [->height]]
            [gdl.map-properties :as map-properties]))

(defn grid->tiled-map
  [schema-tiled-map grid]
  (let [copy-tile (memoize
                   (fn [tile]
                     (assert tile)
                     (static-tiled-map-tile/new tile)))]
    {:properties (merge (map-properties/clojurize (tiled-map/get-properties schema-tiled-map))
                        {"width" (->width grid)
                         "height" (->height grid)})
     :layers (for [layer (tiled-map/get-layers schema-tiled-map)]
               {:name (tiled-map-tile-layer/get-name layer)
                :visible? (tiled-map-tile-layer/visible? layer)
                :properties (map-properties/clojurize (tiled-map-tile-layer/get-properties layer))
                :tiles (for [position (posis/f grid)
                             :let [local-position (get grid position)]
                             :when local-position]
                         (when (vector? local-position)
                           (when-let [cell (tiled-map-tile-layer/get-cell layer (local-position 0) (local-position 1))]
                             [position (copy-tile (tiled-map-tile-layer-cell/get-tile cell))])))})}))
