(ns moon.readable
  (:require [moon.math :as math]))

(defn number [^double x]
  {:pre [(number? x)]}
  (if (or
       (> x 5)
       (math/approx-numbers x (int x) 0.001))
    (int x)
    (math/round-n-decimals x 2)))
