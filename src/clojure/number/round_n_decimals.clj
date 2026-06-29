(ns clojure.number.round-n-decimals
  (:require [clojure.math :as math]))

(defn f [^double x n]
  (let [z (math/pow 10 n)]
    (float
     (/
      (math/round (* x z))
      z))))
