(ns moon.malli)

(defmulti form (fn [[k] _schemas]
                 k))
