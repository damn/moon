(ns clojure.not-enough-mana
  (:require [clojure.get-mana :as get-mana]))

(defn f
  [stats {:keys [skill/cost]}]
  (> cost ((get-mana/f stats) 0)))
