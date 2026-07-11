(ns clojure.grid-to-tiled-map
  (:require [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer$cell :as tiled-map-tile-layer-cell]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile-layer :as tiled-map-tile-layer]
            [com.badlogic.gdx.maps.tiled.tiled-map-tile :as tiled-map-tile]
            [com.badlogic.gdx.maps.tiled.tiled-map :as tiled-map]
            [com.badlogic.gdx.maps.tiled.tiles.static-tiled-map-tile :as static-tiled-map-tile]
            [moon.g2d :as g2d]
            [clojure.g2d.width :refer [->width]]
            [clojure.g2d.height :refer [->height]]
            [gdl.maps.map-properties :as map-properties]))

(defn grid->tiled-map
  [schema-tiled-map grid]
  (let [copy-tile (memoize
                   (fn [tile]
                     (assert tile)
                     (static-tiled-map-tile/new tile)))]
    {:properties (merge (map-properties/clojurize (tiled-map/getProperties schema-tiled-map))
                        {"width" (->width grid)
                         "height" (->height grid)})
     :layers (for [layer (tiled-map/getLayers schema-tiled-map)]
               {:name (tiled-map-tile-layer/getName layer)
                :visible? (tiled-map-tile-layer/isVisible layer)
                :properties (map-properties/clojurize (tiled-map-tile-layer/getProperties layer))
                :tiles (for [position (g2d/posis grid)
                             :let [local-position (get grid position)]
                             :when local-position]
                         (when (vector? local-position)
                           (when-let [cell (tiled-map-tile-layer/getCell layer (local-position 0) (local-position 1))]
                             [position (copy-tile (tiled-map-tile-layer-cell/getTile cell))])))})}))
