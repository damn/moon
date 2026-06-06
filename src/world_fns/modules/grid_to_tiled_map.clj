(ns world-fns.modules.grid-to-tiled-map
  (:require [clojure.to-clj :refer [->clj]]
            [gdx.maps.tiled.tiled-map.get-layers :refer [get-layers]]
            [gdx.maps.tiled.tiled-map-tile-layer.visible :refer [visible?]]
            [clojure.get-properties :refer [get-properties]]
            [gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]
            [gdx.maps.tiled.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [clojure.get-name :refer [get-name]]
            [gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
            [clojure.grid2d :as g2d]))

(def copy-tile
  (memoize
   (fn [tile]
     (assert tile)
     (static-tiled-map-tile/copy tile))))

(defn grid->tiled-map
  [schema-tiled-map grid]
  {:properties (merge (->clj (get-properties schema-tiled-map))
                      {"width" (g2d/width grid)
                       "height" (g2d/height grid)})
   :layers (for [layer (get-layers schema-tiled-map)]
             {:name (get-name layer)
              :visible? (visible? layer)
              :properties (->clj (get-properties layer))
              :tiles (for [position (g2d/posis grid)
                           :let [local-position (get grid position)]
                           :when local-position]
                       (when (vector? local-position)
                         (when-let [cell (get-cell layer local-position)]
                           [position (copy-tile (cell/tile cell))])))})})
