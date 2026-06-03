(ns schema.malli-form.some
  (:require [moon.schemas.malli-form :refer [malli-form]]))

(defmethod malli-form :s/some [_ _schemas]
  :some)
