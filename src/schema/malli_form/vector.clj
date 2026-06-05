(ns schema.malli-form.vector
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/vector [[_ & params] _schemas]
  (apply vector :vector params))
