(ns clojure.modifiers-get-value
  (:require [moon.ops :as ops]))

(defn f [base-value modifiers modifier-k]
  {:pre [(= "modifier" (namespace modifier-k))]}
  (ops/apply (modifier-k modifiers)
           base-value))
