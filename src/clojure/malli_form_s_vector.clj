(ns clojure.malli-form-s-vector
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/vector
  [[_ & params] _]
  (apply vector :vector params))
