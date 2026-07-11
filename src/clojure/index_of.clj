(ns clojure.index-of
  (:import (clojure.lang PersistentVector)))

; TODO java.util.List really?

(defn f [^PersistentVector v k]
  (let [idx (.indexOf v k)]
    (if (= -1 idx)
      nil
      idx)))
