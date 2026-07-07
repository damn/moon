(ns clojure.get-mana
  (:require [clojure.apply-max :as apply-max]))

(defn f
  [{:keys [stats/mana
           stats/modifiers]}]
  (apply-max/f mana modifiers :modifier/mana-max))
