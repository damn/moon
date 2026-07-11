(ns moon.stats
  (:require [clojure.modifiers-get-value :as get-value]
            [clojure.val-max.apply-max :as apply-max]))

(defn get-hitpoints
  [{:keys [stats/hp
           stats/modifiers]}]
  (apply-max/f hp modifiers :modifier/hp-max))

(defn get-mana
  [{:keys [stats/mana
           stats/modifiers]}]
  (apply-max/f mana modifiers :modifier/mana-max))

(defn get-value
  [stats stat-k]
  (when-let [base-value (stat-k stats)]
    (get-value/f base-value
                 (:stats/modifiers stats)
                 (keyword "modifier" (name stat-k)))))
