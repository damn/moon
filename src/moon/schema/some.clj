(ns moon.schema.some
  (:require [moon.malli :as malli]))

(defmethod malli/form :s/some [_ _schemas]
  :some)
