(ns world-fns.uf-caves.assoc-transition-cells
  (:require [clojure.grid2d :as g2d]
            [clojure.grid2d.adjacent-wall-positions :as adjacent-wall-positions]))

(defn f [grid]
  (let [grid (reduce #(assoc %1 %2 :transition) grid
                     (adjacent-wall-positions/f grid))]
    (assert (or
             (= #{:wall :ground :transition} (set (g2d/cells grid)))
             (= #{:ground :transition}       (set (g2d/cells grid))))
            (str "(set (g2d/cells grid)): " (set (g2d/cells grid))))
    ;_ (printgrid/f grid)
    ;_ (println)
    grid))
