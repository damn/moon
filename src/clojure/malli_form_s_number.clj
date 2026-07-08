(ns clojure.malli-form-s-number
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/number
  [[_ predicate] _]
  (case predicate
    :int     int?
    :nat-int nat-int?
    :any     number?
    :pos     pos?
    :pos-int pos-int?))
