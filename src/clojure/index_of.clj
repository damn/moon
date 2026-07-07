(ns clojure.index-of
  (:import (clojure.lang PersistentVector)))

(defn f [^PersistentVector v k]
  (let [idx (.indexOf v k)]
    (if (= -1 idx)
      nil
      idx)))
