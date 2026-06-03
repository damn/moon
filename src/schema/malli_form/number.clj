(ns schema.malli-form.number
  (:require [moon.schemas.malli-form :refer [malli-form]]))

(defmethod malli-form :s/number [[_ predicate] _schemas]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))
