(ns moon.vector
  (:import (clojure.lang PersistentVector)))

(defn index-of [k ^PersistentVector v]
  (let [idx (.indexOf v k)]
    (if (= -1 idx)
      nil
      idx)))
