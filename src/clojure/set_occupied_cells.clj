(ns clojure.set-occupied-cells
  (:require [moon.grid :as grid]))

(defn set-occupied-cells! [grid eid]
  (let [cells (grid/body->occupied-cells grid (:entity/body @eid))]
    (doseq [cell cells]
      (assert (not (get (:occupied @cell) eid)))
      (swap! cell update :occupied conj eid))
    (swap! eid assoc :entity/occupied-cells cells)))
