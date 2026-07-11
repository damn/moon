(ns clojure.grid.inside-cell
  (:require [moon.body :as body]
            [moon.g2d :as g2d]))

(defn inside-cell? [grid entity cell]
  (let [cells (g2d/get-cells grid (body/touched-tiles (:entity/body entity)))]
    (and (= 1 (count cells))
         (= cell (first cells)))))
