(ns moon.modifiers
  (:refer-clojure :exclude [remove])
  (:require [moon.ops.add :as add]
            [moon.ops.remove :as remove]
            [moon.ops.apply :as apply]))

(defn get-value [base-value modifiers modifier-k]
  {:pre [(= "modifier" (namespace modifier-k))]}
  (apply/f (modifier-k modifiers)
           base-value))

(defn add    [mods other-mods] (merge-with add/f    mods other-mods))
(defn remove [mods other-mods] (merge-with remove/f mods other-mods))
