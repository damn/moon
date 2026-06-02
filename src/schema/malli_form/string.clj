(ns schema.malli-form.string
  (:require [moon.schemas :refer [malli-form]]))

(defmethod malli-form :s/string [_ _schemas]
  :string)
