(ns moon.grid.npc-pathing.inside-cell
  (:require [grid2d.get-cells :refer [get-cells]]
            [moon.body.touched-tiles :refer [touched-tiles]]))

(defn f [grid entity cell]
  (let [cells (get-cells grid (touched-tiles (:entity/body entity)))]
    (and (= 1 (count cells))
         (= cell (first cells)))))
