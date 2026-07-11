(ns moon.coll
  (:import (clojure.lang PersistentVector)))

(defn indexed
  "Returns a lazy sequence of [index, item] pairs, where items come
  from 's' and indexes count up from zero.

  (indexed '(a b c d)) => ([0 a] [1 b] [2 c] [3 d])"
  [coll]
  (map vector (iterate inc 0) coll))

(defn positions
  "Returns a lazy sequence containing the positions at which pred
  is true for items in coll."
  [pred coll]
  (for [[idx elt] (indexed coll)
        :when (pred elt)]
    idx))

; TODO use interpose?
(defn interpose-f [f coll]
  (drop 1 (interleave (repeatedly f) coll)))

(defn sort-by-k-order [k-order components]
  (let [max-count (inc (count k-order))]
    (sort-by (fn [[k _]]
               (or (let [idx (.indexOf ^PersistentVector k-order k)]
                     (when (not= -1 idx) idx))
                   max-count))
             components)))

(defn sort-by-order [coll get-item-order-k order]
  (sort-by #((get-item-order-k %) order) < coll))
