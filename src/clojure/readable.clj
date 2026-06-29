(ns clojure.readable
  (:require [clojure.number.is-nearly-equal :as nearly-equal?]
            [clojure.number.round-n-decimals :as round-n-decimals]))

(defn f [^double x]
  {:pre [(number? x)]}
  (if (or
       (> x 5)
       (nearly-equal?/f x (int x) 0.001))
    (int x)
    (round-n-decimals/f x 2)))
