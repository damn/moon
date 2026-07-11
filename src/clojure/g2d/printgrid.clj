(ns clojure.g2d.printgrid
  (:require [moon.g2d :as g2d]))

(defn f
  "Prints with y-up coordinates."
  [grid]
  (doseq [y (range (dec (g2d/height grid)) -1 -1)]
    (doseq [x (range (g2d/width grid))]
      (let [celltype (grid [x y])]
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
                   :transition       "+")))))
    (println)))
