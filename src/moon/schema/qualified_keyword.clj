(ns moon.schema.qualified-keyword
  (:require [moon.schema :as schema]))

(defmethod schema/malli-form :s/qualified-keyword [[_ & params] _schemas]
  (apply vector :qualified-keyword params))
