(ns rand.srand-int
  (:require [rand.srand :refer [srand]]))

(defn srand-int [n random]
  (int (srand n random)))
