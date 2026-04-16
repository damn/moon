(ns moon.create.schema
  (:require [moon.malli :as m]))

(defn step [ctx schema]
  (assoc ctx :ctx/schema (m/schema schema)))
