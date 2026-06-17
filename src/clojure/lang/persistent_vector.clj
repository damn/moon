(ns clojure.lang.persistent-vector
  (:import (clojure.lang PersistentVector)))

(defn index-of [^PersistentVector v k]
  (let [idx (.indexOf v k)]
    (if (= -1 idx)
      nil
      idx)))
