(ns moon.stats
  (:require [malli.core :as m]
            [moon.modifiers :as modifiers]
            [moon.val-max :as val-max]))

(defn- ->pos-int [val-max]
  (mapv #(-> % int (max 0)) val-max))

; TODO can just pass ops instead of modifiers modifier-k
(defn- apply-max [val-max modifiers modifier-k]
  (assert (m/validate val-max/schema val-max) val-max)
  (let [val-max (update val-max 1 modifiers/get-value modifiers modifier-k)
        [v mx] (->pos-int val-max)
        result [(min v mx) mx]]
    (assert (m/validate val-max/schema result) result)
    result))

; TODO can just pass ops instead of modifiers modifier-k
(defn- apply-min [val-max modifiers modifier-k]
  (assert (m/validate val-max/schema val-max) val-max)
  (let [val-max (update val-max 0 modifiers/get-value modifiers modifier-k)
        [v mx] (->pos-int val-max)
        result [v (max v mx)]]
    (assert (m/validate val-max/schema result) result)
    result))

(defn get-stat-value [stats stat-k]
  (when-let [base-value (stat-k stats)]
    (modifiers/get-value base-value
                         (:stats/modifiers stats)
                         (keyword "modifier" (name stat-k)))))

(defn add [stats mods]
  (update stats :stats/modifiers modifiers/add mods))

(defn remove-mods [stats mods]
  (update stats :stats/modifiers modifiers/remove mods))

(defn get-mana [{:keys [stats/mana
                        stats/modifiers]}]
  (apply-max mana modifiers :modifier/mana-max))

(defn not-enough-mana? [stats {:keys [skill/cost]}]
  (> cost ((get-mana stats) 0)))

(defn pay-mana-cost [stats cost]
  (let [mana-val ((get-mana stats) 0)]
    (assert (<= cost mana-val))
    (assoc-in stats [:stats/mana 0] (- mana-val cost))))

(defn get-hitpoints [{:keys [stats/hp
                             stats/modifiers]}]
  (apply-max hp modifiers :modifier/hp-max))

(defn calc-damage
  ([source target damage]
   (update (calc-damage source damage)
           :damage/min-max
           apply-max
           (:stats/modifiers target)
           :modifier/damage-receive-max))
  ([source damage]
   (update damage
           :damage/min-max
           #(-> %
                (apply-min (:stats/modifiers source) :modifier/damage-deal-min)
                (apply-max (:stats/modifiers source) :modifier/damage-deal-max)))))
