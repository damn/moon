(ns moon.stats.not-enough-mana
  (:require [moon.stats.get-mana :as get-mana]))

(defn f
  [stats {:keys [skill/cost]}]
  (> cost ((get-mana/f stats) 0)))
