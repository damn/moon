(ns clojure.malli-form-s-string
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/string
  [_ _]
  :string)
