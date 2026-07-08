(ns clojure.caves.adj-num.thin
  (:require [clojure.srand-int :refer [srand-int]]))

(defn f [open-paths random]
  (if (= open-paths 1)
    1
    (case (int (srand-int 7 random))
      0 0
      1 2
      1)))
