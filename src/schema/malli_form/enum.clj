(ns schema.malli-form.enum
  (:require [moon.schemas :refer [malli-form]]))

(defmethod malli-form :s/enum [[_ & params] _schemas]
  (apply vector :enum params))
