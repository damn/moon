(ns world-fns.modules.grid-to-tiled-map
  (:require [com.badlogic.gdx.maps.map-properties :as props]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer.cell :as cell]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
            [moon.grid2d :as g2d]))

(def copy-tile
  (memoize
   (fn [tile]
     (assert tile)
     (static-tiled-map-tile/copy tile))))

(defn grid->tiled-map
  [schema-tiled-map grid]
  {:properties (merge (props/->clj (tiled-map/props schema-tiled-map))
                      {"width" (g2d/width grid)
                       "height" (g2d/height grid)})
   :layers (for [layer (tiled-map/layers schema-tiled-map)]
             {:name (layer/name layer)
              :visible? (layer/visible? layer)
              :properties (props/->clj (layer/properties layer))
              :tiles (for [position (g2d/posis grid)
                           :let [local-position (get grid position)]
                           :when local-position]
                       (when (vector? local-position)
                         (when-let [cell (layer/cell layer local-position)]
                           [position (copy-tile (cell/tile cell))])))})})
