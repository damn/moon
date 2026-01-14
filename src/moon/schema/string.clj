(ns moon.schema.string
  (:require [moon.schema :as schema]))

(defmethod schema/malli-form :s/string [_ _schemas]
  :string)
