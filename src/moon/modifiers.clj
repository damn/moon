(ns moon.modifiers
  (:refer-clojure :exclude [remove])
  (:require [moon.ops :as ops]))

(defn get-value [base-value modifiers modifier-k]
  {:pre [(= "modifier" (namespace modifier-k))]}
  (ops/apply (modifier-k modifiers)
             base-value))

(defn add    [mods other-mods] (merge-with ops/add    mods other-mods))
(defn remove [mods other-mods] (merge-with ops/remove mods other-mods))
