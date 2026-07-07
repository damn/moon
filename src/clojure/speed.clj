(ns clojure.speed
  (:require [clojure.get-stat-value :refer [get-stat-value]]))

(defn f [{:keys [entity/stats]}]
  (or (get-stat-value stats :stats/movement-speed)
      0))
