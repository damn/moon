(ns moon.schema.string
  (:require [moon.malli :as malli]))

(defmethod malli/form :s/string [_ _schemas]
  :string)
