(ns clojure.moon.handle-clicked-inventory-cell
  (:require [clojure.moon.clicked-inventory-cell :refer [k->clicked-inventory-cell]]))

(defn f
  [player-eid cell]
  (let [state-k (:state (:entity/fsm @player-eid))]
    (when-let [handler (k->clicked-inventory-cell state-k)]
      (handler player-eid cell))))
