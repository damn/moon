(ns moon.schemas.malli-form)

(defmulti malli-form (fn [[k] _schemas]
                       k))
