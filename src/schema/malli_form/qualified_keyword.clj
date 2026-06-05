(ns schema.malli-form.qualified-keyword
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/qualified-keyword [[_ & params] _schemas]
  (apply vector :qualified-keyword params))
