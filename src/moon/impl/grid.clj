(ns moon.impl.grid
  (:require [moon.grid2d :as g2d]
            [moon.tiled-map :as tiled-map]
            [moon.cell :as cell]))

(defn create [{:keys [ctx/tiled-map]}]
  (g2d/create-grid (tiled-map/width tiled-map)
                   (tiled-map/height tiled-map)
                   (fn [position]
                     (atom (cell/create position
                                        (case (tiled-map/movement-property tiled-map position)
                                          "none" :none
                                          "air"  :air
                                          "all"  :all))))))
