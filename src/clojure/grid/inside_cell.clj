(ns clojure.grid.inside-cell
  (:require [clojure.body.touched-tiles :refer [touched-tiles]]
            [clojure.g2d.get-cells :refer [get-cells]]))

(defn inside-cell? [grid entity cell]
  (let [cells (get-cells grid (touched-tiles (:entity/body entity)))]
    (and (= 1 (count cells))
         (= cell (first cells)))))
