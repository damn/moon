(ns clojure.indexed)

; coll

(defn indexed
  "Returns a lazy sequence of [index, item] pairs, where items come
  from 's' and indexes count up from zero.

  (indexed '(a b c d)) => ([0 a] [1 b] [2 c] [3 d])"
  [coll]
  (map vector (iterate inc 0) coll))
