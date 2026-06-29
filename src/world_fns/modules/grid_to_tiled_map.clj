(ns world-fns.modules.grid-to-tiled-map
  (:require [gdx.maps.map-properties :as map-properties]
            [tiled-map.get-layers :refer [get-layers]]
            [tiled.static-tiled-map-tile.copy :as copy]
            [grid2d.posis :as posis]
            [grid2d.width :refer [->width]]
            [grid2d.height :refer [->height]])
  (:import (com.badlogic.gdx.maps.tiled TiledMap
                                        TiledMapTileLayer$Cell)))

(defn grid->tiled-map
  [schema-tiled-map grid]
  (let [copy-tile (memoize
                   (fn [tile]
                     (assert tile)
                     (copy/f tile)))]
    {:properties (merge (map-properties/clojurize (TiledMap/.getProperties schema-tiled-map))
                        {"width" (->width grid)
                         "height" (->height grid)})
     :layers (for [layer (get-layers schema-tiled-map)]
               {:name (.getName layer)
                :visible? (.isVisible layer)
                :properties (map-properties/clojurize (.getProperties layer))
                :tiles (for [position (posis/f grid)
                             :let [local-position (get grid position)]
                             :when local-position]
                         (when (vector? local-position)
                           (when-let [cell (.getCell layer (local-position 0) (local-position 1))]
                             [position (copy-tile (.getTile ^TiledMapTileLayer$Cell cell))])))})}))
