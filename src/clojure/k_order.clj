(ns clojure.k-order
  (:require [clojure.v :as v]))

(defn sort-by-k-order [k-order components]
  (let [max-count (inc (count k-order))]
    (sort-by (fn [[k _]] (or (v/index-of k-order k) max-count))
             components)))
