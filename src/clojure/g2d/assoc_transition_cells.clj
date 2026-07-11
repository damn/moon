(ns clojure.g2d.assoc-transition-cells
  (:require [clojure.g2d.cells :refer [->cells]]
            [moon.g2d :as g2d]))

(defn f [grid]
  (let [grid (reduce #(assoc %1 %2 :transition) grid
                     (g2d/adjacent-wall-positions grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (->cells grid)))
             (= #{:ground :transition}       (set (->cells grid))))
            (str "(set (->cells grid)): " (set (->cells grid))))
    ;_ (printgrid/f grid)
    ;_ (println)
    grid))
