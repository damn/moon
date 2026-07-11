(ns moon.schema.vector
  (:require [moon.schema :as schema]))

(defmethod schema/malli-form :s/vector
  [[_ & params] _]
  (apply vector :vector params))
