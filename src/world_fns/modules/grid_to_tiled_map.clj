(ns world-fns.modules.grid-to-tiled-map
  (:require [map-properties.clojurize :as clojurize]
            [gdl.get-layers :refer [get-layers]]
            [gdl.tiled-map-tile-layer.visible :refer [visible?]]
            [tiled-map-tile-layer.get-properties :as get-properties]
            [tiled-map.get-properties :as tiled-map-get-properties]
            [gdl.get-tile :as get-tile]
            [gdl.tiled-map-tile-layer.get-cell :refer [get-cell]]
            [gdl.tiled-map-tile-layer.get-name :refer [get-name]]
            [gdl.static-tiled-map-tile :as static-tiled-map-tile]
            [clojure.grid2d.posis :as posis]
            [clojure.grid2d.width :refer [->width]]
            [clojure.grid2d.height :refer [->height]]))

(def copy-tile
  (memoize
   (fn [tile]
     (assert tile)
     (static-tiled-map-tile/copy tile))))

(defn grid->tiled-map
  [schema-tiled-map grid]
  {:properties (merge (clojurize/f (tiled-map-get-properties/f schema-tiled-map))
                      {"width" (->width grid)
                       "height" (->height grid)})
   :layers (for [layer (get-layers schema-tiled-map)]
             {:name (get-name layer)
              :visible? (visible? layer)
              :properties (clojurize/f (get-properties/f layer))
              :tiles (for [position (posis/f grid)
                           :let [local-position (get grid position)]
                           :when local-position]
                       (when (vector? local-position)
                         (when-let [cell (get-cell layer local-position)]
                           [position (copy-tile (get-tile/f cell))])))})})
