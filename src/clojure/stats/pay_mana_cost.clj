(ns clojure.stats.pay-mana-cost
  (:require [moon.stats :as stats]))

(defn f [stats cost]
  (let [mana-val ((stats/get-mana stats) 0)]
    (assert (<= cost mana-val))
    (assoc-in stats [:stats/mana 0] (- mana-val cost))))
