(ns clojure.caves.adj-num.default
  (:require [clojure.srand-int :refer [srand-int]]))

(defn f [open-paths random]
  (if (= open-paths 1)
    (case (int (srand-int 4 random))
      0 1
      1 1
      2 1
      3 2
      1)
    (case (int (srand-int 4 random))
      0 0
      1 1
      2 1
      3 2
      1)))
