(ns moon.schema.boolean
  (:require [moon.malli :as malli]))

(defmethod malli/form :s/boolean [_ _schemas]
  :boolean)
