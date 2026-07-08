(ns clojure.malli-form)

(defmulti malli-form (fn [schema _schemas]
                       (first schema)))
