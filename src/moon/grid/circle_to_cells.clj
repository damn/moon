(ns moon.grid.circle-to-cells
  (:require [clojure.outer-rectangle :refer [outer-rectangle]]
            [clojure.touched-tiles :refer [touched-tiles]]
            [clojure.get-cells :refer [get-cells]]))

(defn circle->cells [g2d circle]
  (->> circle
       outer-rectangle
       touched-tiles
       (get-cells g2d)))
