(ns clojure.malli-form)


; use data -k-fn dispatch table

(defmulti malli-form (fn [schema _schemas]
                       (first schema)))
