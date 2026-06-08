(ns moon.stats.get-mana
  (:require [moon.val-max.apply-max :as apply-max]))

(defn f
  [{:keys [stats/mana
           stats/modifiers]}]
  (apply-max/f mana modifiers :modifier/mana-max))
