(ns moon.caves.adj-num.wide
  (:require [rand.srand-int :refer [srand-int]]))

(defn f [open-paths random]
  (if (= open-paths 1)
    (case (int (srand-int 3 random))
      0 1
      2)
    (case (int (srand-int 4 random))
      0 1
      1 2
      2 3
      3 4
      1)))
