(ns clojure.create-value)

(defmulti create-value (fn [[k] _v _db]
                         k))
