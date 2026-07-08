(ns clojure.malli-form-s-enum
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/enum
  [[_ & params] _]
  (apply vector :enum params))
