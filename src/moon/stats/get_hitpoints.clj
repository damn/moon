(ns moon.stats.get-hitpoints
  (:require [moon.val-max.apply-max :as apply-max]))

(defn f
  [{:keys [stats/hp
           stats/modifiers]}]
  (apply-max/f hp modifiers :modifier/hp-max))
