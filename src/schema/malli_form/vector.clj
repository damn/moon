(ns schema.malli-form.vector
  (:require [moon.schemas :refer [malli-form]]))

(defmethod malli-form :s/vector [[_ & params] _schemas]
  (apply vector :vector params))
