(ns clojure.malli-form-s-qualified-keyword
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/qualified-keyword
  [[_ & params] _]
  (apply vector :qualified-keyword params))
