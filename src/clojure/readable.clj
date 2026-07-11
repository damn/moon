(ns clojure.readable
  (:require [moon.number :as number]
            [clojure.round-n-decimals :as round-n-decimals]))

(defn f [^double x]
  {:pre [(number? x)]}
  (if (or
       (> x 5)
       (number/nearly-equal? x (int x) 0.001))
    (int x)
    (round-n-decimals/f x 2)))
