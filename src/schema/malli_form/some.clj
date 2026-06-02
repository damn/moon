(ns schema.malli-form.some
  (:require [moon.schemas :refer [malli-form]]))

(defmethod malli-form :s/some [_ _schemas]
  :some)
