(ns moon.number
  (:require [clojure.math :as math]))

(def ^:private float-rounding-error (double 0.000001))

(defn nearly-equal?
  ([x y]
   (nearly-equal? x y float-rounding-error))
  ([a b epsilon]
   (<= (Math/abs (- a b))
       epsilon)))

(defn round-n [^double x n]
  (let [z (math/pow 10 n)]
    (float
     (/
      (math/round (* x z))
      z))))
