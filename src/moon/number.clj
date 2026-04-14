(ns moon.number
  (:require [clojure.math :as math]))

(def float-rounding-error (double 0.000001)) ; FIXME clojure uses doubles?

(defn nearly-equal? [^double x ^double y]
  (<= (Math/abs (- x y)) float-rounding-error))

(defn approx-numbers [a b epsilon]
  (<= (Math/abs (double (- a b))) epsilon))

(defn round-n-decimals [^double x n]
  (let [z (math/pow 10 n)]
    (float
     (/
      (math/round (* x z))
      z))))

(defn readable [^double x]
  {:pre [(number? x)]}
  (if (or
       (> x 5)
       (approx-numbers x (int x) 0.001))
    (int x)
    (round-n-decimals x 2)))
