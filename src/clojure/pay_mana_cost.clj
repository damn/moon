(ns clojure.pay-mana-cost
  (:require [clojure.stats.get-mana :as get-mana]))

(defn f [stats cost]
  (let [mana-val ((get-mana/f stats) 0)]
    (assert (<= cost mana-val))
    (assoc-in stats [:stats/mana 0] (- mana-val cost))))
