(ns moon.schema.val-max
  (:require [moon.schema :as moon-schema]
            [moon.val-max :as val-max]))

(defmethod moon-schema/malli-form :s/val-max
  [_ _]
  val-max/schema)
