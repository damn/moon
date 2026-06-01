(ns clojure.grid2d.printgrid
  (:require [clojure.grid2d :as g2d]))

(defn- print-cell [celltype]
  (print (if (number? celltype)
           celltype
           (case celltype
             nil               "?"
             :undefined        " "
             :ground           "_"
             :wall             "#"
             :airwalkable      "."
             :module-placement "X"
             :start-module     "@"
             :transition       "+"))))

(defn f ; print-grid in data.grid2d is y-down
  "Prints with y-up coordinates."
  [grid]
  (doseq [y (range (dec (g2d/height grid)) -1 -1)]
    (doseq [x (range (g2d/width grid))]
      (print-cell (grid [x y])))
    (println)))
