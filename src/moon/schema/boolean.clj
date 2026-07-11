(ns moon.schema.boolean
  (:require [moon.schema :as schema]))

(defmethod schema/malli-form :s/boolean
  [_ _]
  :boolean)
