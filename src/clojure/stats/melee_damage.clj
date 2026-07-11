(ns clojure.stats.melee-damage
  (:require [moon.stats :as stats]))

; TODO pass stats directly
(defn f [{:keys [entity/stats]}]
  (let [strength (or (stats/get-value stats :stats/strength) 0)]
    {:damage/min-max [strength strength]}))
