(ns moon.schema.vector
  (:require [moon.malli :as malli]))

(defmethod malli/form :s/vector [[_ & params] _schemas]
  (apply vector :vector params))
