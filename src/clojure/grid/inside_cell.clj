(ns clojure.grid.inside-cell
  (:require [moon.body :as body]
            [clojure.g2d.get-cells :refer [get-cells]]))

(defn inside-cell? [grid entity cell]
  (let [cells (get-cells grid (body/touched-tiles (:entity/body entity)))]
    (and (= 1 (count cells))
         (= cell (first cells)))))
