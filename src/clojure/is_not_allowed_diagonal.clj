(ns clojure.is-not-allowed-diagonal
  (:require [clojure.diagonal-check-indizes :as diagonal-check-indizes]))

(defn f? [at-idx adjacent-cells]
  (when-let [[a b] (get diagonal-check-indizes/v at-idx)]
    (and (nil? (adjacent-cells a))
         (nil? (adjacent-cells b)))))
