(ns clojure.sort-by-k-order
  (:require [clojure.vector.index-of :as index-of]))

(defn sort-by-k-order [k-order components]
  (let [max-count (inc (count k-order))]
    (sort-by (fn [[k _]] (or (index-of/f k-order k) max-count))
             components)))
