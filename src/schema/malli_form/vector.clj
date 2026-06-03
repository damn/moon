(ns schema.malli-form.vector
  (:require [moon.schemas.malli-form :refer [malli-form]]))

(defmethod malli-form :s/vector [[_ & params] _schemas]
  (apply vector :vector params))
