(ns clojure.readable
  (:require [moon.number :as number]))

(defn f [^double x]
  {:pre [(number? x)]}
  (if (or
       (> x 5)
       (number/nearly-equal? x (int x) 0.001))
    (int x)
    (number/round-n x 2)))
