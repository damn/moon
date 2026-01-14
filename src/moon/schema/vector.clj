(ns moon.schema.vector
  (:require [moon.schema :as schema]))

(defmethod schema/malli-form :s/vector [[_ & params] _schemas]
  (apply vector :vector params))
