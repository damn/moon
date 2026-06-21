(ns clojure.readable
  (:require [moon.number :refer [approx-numbers
                                 round-n-decimals]]))

(defn readable [^double x]
  {:pre [(number? x)]}
  (if (or
       (> x 5)
       (approx-numbers x (int x) 0.001))
    (int x)
    (round-n-decimals x 2)))
