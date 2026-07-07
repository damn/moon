(ns clojure.readable
  (:require [clojure.is-nearly-equal :as nearly-equal?]
            [clojure.round-n-decimals :as round-n-decimals]))

(defn f [^double x]
  {:pre [(number? x)]}
  (if (or
       (> x 5)
       (nearly-equal?/f x (int x) 0.001))
    (int x)
    (round-n-decimals/f x 2)))
