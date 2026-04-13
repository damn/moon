(ns moon.create.explored-tile-corners
  (:require [moon.grid2d :as g2d]
            [moon.tiled-map :as tiled-map]))

(defn step [{:keys [ctx/tiled-map] :as ctx}]
  (assoc ctx :ctx/explored-tile-corners (atom (g2d/create-grid (tiled-map/width tiled-map)
                                                               (tiled-map/height tiled-map)
                                                               (constantly false)))))
