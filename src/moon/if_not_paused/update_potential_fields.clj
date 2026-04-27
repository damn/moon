(ns moon.if-not-paused.update-potential-fields
  (:require [moon.grid :as grid]))

(defn step
  [{:keys [ctx/active-entities
           ctx/factions-iterations
           ctx/grid
           ctx/potential-field-cache]
    :as ctx}]
  (doseq [[faction max-iterations] factions-iterations]
    (grid/tick! potential-field-cache
                grid
                faction
                active-entities
                max-iterations))
  ctx)
