(ns clojure.creature-melee-damage
  (:require [clojure.stats.get-stat-value :refer [get-stat-value]]))

; TODO pass stats directly
(defn f [{:keys [entity/stats]}]
  (let [strength (or (get-stat-value stats :stats/strength) 0)]
    {:damage/min-max [strength strength]}))
