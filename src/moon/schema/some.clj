(ns moon.schema.some
  (:require [moon.schema :as schema]))

(defmethod schema/malli-form :s/some
  [_ _]
  :some)
