(ns moon.grid.circle-to-cells
  (:require [math.circle.outer-rectangle :refer [outer-rectangle]]
            [math.rectangle.touched-tiles :refer [touched-tiles]]
            [grid2d.get-cells :refer [get-cells]]))

(defn circle->cells [g2d circle]
  (->> circle
       outer-rectangle
       touched-tiles
       (get-cells g2d)))
