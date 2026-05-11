(ns moon.impl.explored-tile-corners
  (:require [moon.tiled-map :as tiled-map]
            [moon.grid2d :as g2d]))

(defn create
  [{:keys [ctx/tiled-map]}]
  (atom (g2d/create-grid (tiled-map/width tiled-map)
                         (tiled-map/height tiled-map)
                         (constantly false))))
