(ns moon.schema.enum
  (:require [moon.schema :as schema]))

(defmethod schema/malli-form :s/enum [[_ & params] _schemas]
  (apply vector :enum params))
