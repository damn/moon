(ns create.grid
  (:require [moon.cell :as cell]
            [moon.grid2d :as g2d]
            [moon.tiled-map :as tiled-map]))

(defn step
  [{:keys [ctx/tiled-map]
    :as ctx}]
  (assoc ctx :ctx/grid (g2d/create-grid (.get (.getProperties tiled-map) "width")
                                        (.get (.getProperties tiled-map) "height")
                                        (fn [position]
                                          (atom (cell/create position
                                                             (case (tiled-map/movement-property tiled-map position)
                                                               "none" :none
                                                               "air"  :air
                                                               "all"  :all)))))))
