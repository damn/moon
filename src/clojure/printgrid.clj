(ns clojure.printgrid
  (:require [clojure.height :refer [->height]]
            [clojure.width :refer [->width]]))

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
