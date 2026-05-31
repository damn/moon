(ns clojure.coll.indexed)

(defn f
  "Returns a lazy sequence of [index, item] pairs, where items come
  from 's' and indexes count up from zero.

  (clojure.coll.indexed/f '(a b c d)) => ([0 a] [1 b] [2 c] [3 d])"
  [s]
  (map vector (iterate inc 0) s))
