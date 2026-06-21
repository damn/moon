(ns clojure.grid2d.printgrid
  (:require [clojure.grid2d.height :refer [->height]]
            [clojure.grid2d.width :refer [->width]]))

(defn f
  "Prints with y-up coordinates."
  [grid]
  (doseq [y (range (dec (->height grid)) -1 -1)]
    (doseq [x (range (->width grid))]
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
