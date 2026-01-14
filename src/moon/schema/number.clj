(ns moon.schema.number
  (:require [moon.malli :as malli]))

(defmethod malli/form :s/number [[_ predicate] _schemas]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))
