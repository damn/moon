(ns moon.schema.val-max
  (:require [moon.schema :as schema]
            [moon.val-max :as val-max]))

(defmethod schema/malli-form :s/val-max [_ _schemas]
  val-max/schema)
