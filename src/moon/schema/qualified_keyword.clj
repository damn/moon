(ns moon.schema.qualified-keyword
  (:require [moon.malli :as malli]))

(defmethod malli/form :s/qualified-keyword [[_ & params] _schemas]
  (apply vector :qualified-keyword params))
