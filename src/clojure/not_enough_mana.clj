(ns clojure.not-enough-mana
  (:require [moon.stats :as stats]))

(defn f
  [stats {:keys [skill/cost]}]
  (> cost ((stats/get-mana stats) 0)))
