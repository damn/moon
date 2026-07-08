(ns clojure.malli-form-s-some
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/some
  [_ _]
  :some)
