(ns clojure.if-not-paused-update-potential-fields
  (:require [clojure.factions-iterations :as factions-iterations]
            [clojure.grid-update-potential-fields :as update-potential-fields]))

(defn f
  [{:keys [ctx/active-entities
           ctx/grid
           ctx/potential-field-cache]
    :as ctx}]
  (doseq [[faction max-iterations] factions-iterations/factions-iterations]
    (update-potential-fields/tick! grid
                                   potential-field-cache
                                   faction
                                   active-entities
                                   max-iterations))
  ctx)
