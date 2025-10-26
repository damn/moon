; Use schema, pre/post, tests for understanding.
; e.g. ops just :ops/inc/:ops/mult?
(ns cdq.entity.stats
  (:require [cdq.stats.ops :as ops]
            [moon.val-max :as val-max]
            [malli.core :as m]))

(defn- get-value [base-value modifiers modifier-k]
  {:pre [(= "modifier" (namespace modifier-k))]}
  (ops/apply (modifier-k modifiers)
             base-value))

(defn- ->pos-int [val-max]
  (mapv #(-> % int (max 0)) val-max))

; TODO can just pass ops instead of modifiers modifier-k
(defn apply-max [val-max modifiers modifier-k]
  (assert (m/validate val-max/schema val-max) val-max)
  (let [val-max (update val-max 1 get-value modifiers modifier-k)
        [v mx] (->pos-int val-max)
        result [(min v mx) mx]]
  (assert (m/validate val-max/schema result) result)
  result))

; TODO can just pass ops instead of modifiers modifier-k
(defn apply-min [val-max modifiers modifier-k]
  (assert (m/validate val-max/schema val-max) val-max)
  (let [val-max (update val-max 0 get-value modifiers modifier-k)
        [v mx] (->pos-int val-max)
        result [v (max v mx)]]
    (assert (m/validate val-max/schema result) result)
    result))

(defn- add*    [mods other-mods] (merge-with ops/add    mods other-mods))
(defn- remove* [mods other-mods] (merge-with ops/remove mods other-mods))

; 1. name ! :entity/ -> :stats/
; 2. tests/protocols -> what are data structure of modifiers => is stat-k
; witha modifier key????
; how does the whole thing look like
; including editor based omgfwtf

(defn get-stat-value [stats stat-k]
  (when-let [base-value (stat-k stats)]
    (get-value base-value
               (:stats/modifiers stats)
               (keyword "modifier" (name stat-k)))))

(defn add    [stats mods] (update stats :stats/modifiers add*    mods))
(defn remove-mods [stats mods] (update stats :stats/modifiers remove* mods))

(defn get-mana
  [{:keys [stats/mana
           stats/modifiers]}]
  (apply-max mana modifiers :modifier/mana-max))

(defn not-enough-mana? [stats {:keys [skill/cost]}]
  (> cost ((get-mana stats) 0)))

(defn pay-mana-cost [stats cost]
  (let [mana-val ((get-mana stats) 0)]
    (assert (<= cost mana-val))
    (assoc-in stats [:stats/mana 0] (- mana-val cost))))

(defn get-hitpoints
  [{:keys [stats/hp
           stats/modifiers]}]
  (apply-max hp modifiers :modifier/hp-max))

(defn create [stats _world]
  (-> stats
      (update :stats/mana (fn [v] [v v]))
      (update :stats/hp   (fn [v] [v v]))))
