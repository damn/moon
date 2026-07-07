(ns clojure.srand-int
  (:require [clojure.srand :refer [srand]]))

(defn srand-int [n random]
  (int (srand n random)))
