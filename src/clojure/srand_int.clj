(ns clojure.srand-int
  (:require [clojure.java.util.random :as random]))

(defn srand-int [n random]
  (int (random/srand n random)))
