(ns moon.creature.apply-action-speed-modifier
  (:require [moon.stats.get-stat-value :refer [get-stat-value]]))

(defn f [{:keys [entity/stats]} skill action-time]
  (/ action-time
     (or (get-stat-value stats (:skill/action-time-modifier-key skill))
         1)))
