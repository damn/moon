(ns clojure.malli-form-s-boolean
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/boolean
  [_ _]
  :boolean)
