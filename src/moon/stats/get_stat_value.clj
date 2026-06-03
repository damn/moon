(ns moon.stats.get-stat-value
  (:require [moon.modifiers :as modifiers]))

(defn get-stat-value [stats stat-k]
  (when-let [base-value (stat-k stats)]
    (modifiers/get-value base-value
                         (:stats/modifiers stats)
                         (keyword "modifier" (name stat-k)))))
