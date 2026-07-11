(ns clojure.malli-form)


; use data -k-fn dispatch table

; clojure - SCHEMA -
(defmulti malli-form (fn [schema _schemas]
                       (first schema)))
