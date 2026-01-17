(ns moon.create.grid
  (:require [clojure.grid2d :as g2d]
            [moon.cell :as cell]
            [moon.tiled-map :as tiled-map]
            [moon.world.create.grid]))

(defn- create-world-grid [width height cell-movement]
  (g2d/create-grid width
                   height
                   (fn [position]
                     (atom (cell/create position (cell-movement position))))))

(defn step [{:keys [ctx/tiled-map] :as ctx}]
  (assoc ctx :ctx/grid (create-world-grid (.get (.getProperties tiled-map) "width")
                                          (.get (.getProperties tiled-map) "height")
                                          #(case (tiled-map/movement-property tiled-map %)
                                             "none" :none
                                             "air"  :air
                                             "all"  :all))))
