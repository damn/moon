(ns clojure.g2d.assoc-transition-cells
  (:require [moon.g2d :as g2d]))

(defn f [grid]
  (let [grid (reduce #(assoc %1 %2 :transition) grid
                     (g2d/adjacent-wall-positions grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (g2d/cells grid)))
             (= #{:ground :transition}       (set (g2d/cells grid))))
            (str "(set (g2d/cells grid)): " (set (g2d/cells grid))))
    ;_ (printgrid/f grid)
    ;_ (println)
    grid))
