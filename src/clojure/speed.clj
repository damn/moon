(ns clojure.speed
  (:require [clojure.stats.get-stat-value :refer [get-stat-value]]))

(defn f [{:keys [entity/stats]}]
  (or (get-stat-value stats :stats/movement-speed)
      0))
