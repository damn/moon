(ns world-fns.modules.grid-to-tiled-map
  (:require [clojure.gdx :as gdx]
            [gdx.maps.map-properties :as map-properties]
            [grid2d.posis :as posis]
            [grid2d.width :refer [->width]]
            [grid2d.height :refer [->height]]))

(defn grid->tiled-map
  [schema-tiled-map grid]
  (let [copy-tile (memoize
                   (fn [tile]
                     (assert tile)
                     (gdx/static-tiled-map-tile-copy tile)))]
    {:properties (merge (map-properties/clojurize (gdx/tiled-map-get-properties schema-tiled-map))
                        {"width" (->width grid)
                         "height" (->height grid)})
     :layers (for [layer (gdx/tiled-map-get-layers schema-tiled-map)]
               {:name (gdx/tiled-map-tile-layer-get-name layer)
                :visible? (gdx/tiled-map-tile-layer-is-visible? layer)
                :properties (map-properties/clojurize (gdx/tiled-map-tile-layer-get-properties layer))
                :tiles (for [position (posis/f grid)
                             :let [local-position (get grid position)]
                             :when local-position]
                         (when (vector? local-position)
                           (when-let [cell (gdx/tiled-map-tile-layer-get-cell layer (local-position 0) (local-position 1))]
                             [position (copy-tile (gdx/cell-get-tile cell))])))})}))
