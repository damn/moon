(ns schema.malli-form.boolean
  (:require [moon.schemas :refer [malli-form]]))

(defmethod malli-form :s/boolean [_ _schemas]
  :boolean)
