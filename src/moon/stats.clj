(ns moon.stats
  (:require [moon.modifiers :as modifiers]
            [moon.val-max.apply-max :as apply-max]))

(defn add [stats mods]
  (update stats :stats/modifiers modifiers/add mods))

(defn remove-mods [stats mods]
  (update stats :stats/modifiers modifiers/remove mods))

(defn get-mana [{:keys [stats/mana
                        stats/modifiers]}]
  (apply-max/f mana modifiers :modifier/mana-max))

(defn not-enough-mana? [stats {:keys [skill/cost]}]
  (> cost ((get-mana stats) 0)))

(defn pay-mana-cost [stats cost]
  (let [mana-val ((get-mana stats) 0)]
    (assert (<= cost mana-val))
    (assoc-in stats [:stats/mana 0] (- mana-val cost))))

(defn get-hitpoints [{:keys [stats/hp
                             stats/modifiers]}]
  (apply-max/f hp modifiers :modifier/hp-max))
