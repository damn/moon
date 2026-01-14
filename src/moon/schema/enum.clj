(ns moon.schema.enum
  (:require [moon.malli :as malli]))

(defmethod malli/form :s/enum [[_ & params] _schemas]
  (apply vector :enum params))
