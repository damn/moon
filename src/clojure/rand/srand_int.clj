(ns clojure.rand.srand-int
  (:require [clojure.rand.srand :refer [srand]]))

(defn srand-int [n random]
  (int (srand n random)))
