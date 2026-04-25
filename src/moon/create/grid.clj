(ns moon.create.grid
  (:require [moon.grid2d :as g2d]
            [moon.cell :as cell]
            [moon.tiled-map :as tiled-map]))

(defn step [{:keys [ctx/tiled-map] :as ctx}]
  (assoc ctx :ctx/grid (g2d/create-grid (tiled-map/width tiled-map)
                                        (tiled-map/height tiled-map)
                                        (fn [position]
                                          (atom (cell/create position
                                                             (case (tiled-map/movement-property tiled-map position)
                                                               "none" :none
                                                               "air"  :air
                                                               "all"  :all)))))))
