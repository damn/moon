(ns clojure.assoc-transition-cells
  (:require [clojure.cells :refer [->cells]]
            [clojure.g2d.adjacent-wall-positions :as adjacent-wall-positions]))

(defn f [grid]
  (let [grid (reduce #(assoc %1 %2 :transition) grid
                     (adjacent-wall-positions/f grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (->cells grid)))
             (= #{:ground :transition}       (set (->cells grid))))
            (str "(set (->cells grid)): " (set (->cells grid))))
    ;_ (printgrid/f grid)
    ;_ (println)
    grid))
