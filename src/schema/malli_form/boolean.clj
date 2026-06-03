(ns schema.malli-form.boolean
  (:require [moon.schemas.malli-form :refer [malli-form]]))

(defmethod malli-form :s/boolean [_ _schemas]
  :boolean)
