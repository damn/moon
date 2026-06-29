(ns moon.stats.get-stat-value
  (:require [moon.modifiers.get-value :as get-value]))

(defn get-stat-value [stats stat-k]
  (when-let [base-value (stat-k stats)]
    (get-value/f base-value
                 (:stats/modifiers stats)
                 (keyword "modifier" (name stat-k)))))
