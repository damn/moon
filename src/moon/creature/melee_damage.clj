(ns moon.creature.melee-damage
  (:require [moon.stats.get-stat-value :refer [get-stat-value]]))

(defn f [{:keys [entity/stats]}]
  (let [strength (or (get-stat-value stats :stats/strength) 0)]
    {:damage/min-max [strength strength]}))
