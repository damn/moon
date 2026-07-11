(ns moon.schema.enum
  (:require [moon.schema :as schema]))

(defmethod schema/malli-form :s/enum
  [[_ & params] _]
  (apply vector :enum params))
