(ns schema.malli-form.enum
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/enum [[_ & params] _schemas]
  (apply vector :enum params))
