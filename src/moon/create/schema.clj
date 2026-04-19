(ns moon.create.schema
  (:require [moon.malli :as m]))

(defn step [ctx malli-form]
  (assoc ctx :ctx/schema (m/schema malli-form)))
