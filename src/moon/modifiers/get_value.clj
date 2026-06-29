(ns moon.modifiers.get-value
  (:require [moon.ops.apply :as apply]))

(defn f [base-value modifiers modifier-k]
  {:pre [(= "modifier" (namespace modifier-k))]}
  (apply/f (modifier-k modifiers)
           base-value))
