(ns clojure.inside-cell
  (:require [clojure.g2d.get-cells :refer [get-cells]]
            [clojure.body.touched-tiles :refer [touched-tiles]]))

(defn f [grid entity cell]
  (let [cells (get-cells grid (touched-tiles (:entity/body entity)))]
    (and (= 1 (count cells))
         (= cell (first cells)))))
