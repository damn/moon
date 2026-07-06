(ns world-fns.modules.grid-to-tiled-map
  (:require
            [com.badlogic.gdx.maps.map-properties :as map-properties]
            [grid2d.posis :as posis]
            [grid2d.width :refer [->width]]
            [grid2d.height :refer [->height]]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
            [clojure.gdx.tiled-map-tile-layer.get-cell :as get-cell]
            [clojure.gdx.tiled-map-tile-layer.get-name :as get-name]
            [clojure.gdx.tiled-map-tile-layer.get-properties :as get-layer-properties]
            [clojure.gdx.tiled-map-tile-layer.is-visible :as is-visible]
            [clojure.gdx.tiled-map-tile-layer$cell.get-tile :as get-tile]
            [clojure.gdx.tiled-map.get-layers :as get-layers]
            [clojure.gdx.tiled-map.get-properties :as get-properties]))

(defn grid->tiled-map
  [schema-tiled-map grid]
  (let [copy-tile (memoize
                   (fn [tile]
                     (assert tile)
                     (static-tiled-map-tile/new-tile tile)))]
    {:properties (merge (map-properties/clojurize (get-properties/f schema-tiled-map))
                        {"width" (->width grid)
                         "height" (->height grid)})
     :layers (for [layer (get-layers/f schema-tiled-map)]
               {:name (get-name/f layer)
                :visible? (is-visible/f layer)
                :properties (map-properties/clojurize (get-layer-properties/f layer))
                :tiles (for [position (posis/f grid)
                             :let [local-position (get grid position)]
                             :when local-position]
                         (when (vector? local-position)
                           (when-let [cell (get-cell/f layer (local-position 0) (local-position 1))]
                             [position (copy-tile (get-tile/f cell))])))})}))
