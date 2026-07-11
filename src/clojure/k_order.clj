(ns clojure.k-order
  (:import (clojure.lang PersistentVector)))

(defn sort-by-k-order [k-order components]
  (let [max-count (inc (count k-order))]
    (sort-by (fn [[k _]]
               (or (let [idx (.indexOf ^PersistentVector k-order k)]
                     (when (not= -1 idx) idx))
                   max-count))
             components)))
