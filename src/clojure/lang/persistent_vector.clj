(ns clojure.lang.persistent-vector
  (:import (clojure.lang PersistentVector)))

(defn indexOf [v k]
  (.indexOf ^PersistentVector v k))
