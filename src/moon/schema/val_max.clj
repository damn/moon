(ns moon.schema.val-max
  (:require [moon.malli :as malli]
            [moon.val-max :as val-max]))

(defmethod malli/form :s/val-max [_ _schemas]
  val-max/schema)
