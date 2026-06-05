(ns clojure.malli-form)

(defmulti malli-form (fn [[k] _schemas]
                       k))
