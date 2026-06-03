(ns schema.malli-form.val-max
  (:require [moon.schemas.malli-form :refer [malli-form]]
            [moon.val-max :as val-max]))

(defmethod malli-form :s/val-max [_ _schemas]
  val-max/schema)
