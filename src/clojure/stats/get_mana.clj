(ns clojure.stats.get-mana
  (:require [clojure.val-max.apply-max :as apply-max]))

(defn f
  [{:keys [stats/mana
           stats/modifiers]}]
  (apply-max/f mana modifiers :modifier/mana-max))
