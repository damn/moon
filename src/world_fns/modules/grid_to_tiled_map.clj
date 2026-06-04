(ns world-fns.modules.grid-to-tiled-map
  (:require [clojure.gdx.maps.map-properties :as props]
            [clojure.gdx.maps.tiled.tiled-map :as tiled-map]
            [clojure.gdx.maps.tiled.tiled-map.get-properties :as tm]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.visible :refer [visible?]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.get-properties :refer [get-properties]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [clojure.gdx.maps.tiled.tiled-map-tile-layer.get-name :refer [get-name]]
            [clojure.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
            [clojure.grid2d :as g2d]))

(def copy-tile
  (memoize
   (fn [tile]
     (assert tile)
     (static-tiled-map-tile/copy tile))))

(defn grid->tiled-map
  [schema-tiled-map grid]
  {:properties (merge (props/->clj (tm/get-properties schema-tiled-map))
                      {"width" (g2d/width grid)
                       "height" (g2d/height grid)})
   :layers (for [layer (tiled-map/layers schema-tiled-map)]
             {:name (get-name layer)
              :visible? (visible? layer)
              :properties (props/->clj (get-properties layer))
              :tiles (for [position (g2d/posis grid)
                           :let [local-position (get grid position)]
                           :when local-position]
                       (when (vector? local-position)
                         (when-let [cell (get-cell layer local-position)]
                           [position (copy-tile (cell/tile cell))])))})})
