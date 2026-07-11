(ns moon.schema)

(defmulti malli-form (fn [schema _schemas]
                       (first schema)))
