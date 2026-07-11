(ns moon.schema.number
  (:require [moon.schema :as schema]))

(defmethod schema/malli-form :s/number
  [[_ predicate] _]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))
