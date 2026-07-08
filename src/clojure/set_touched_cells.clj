(ns clojure.set-touched-cells
  (:require [clojure.body.touched-tiles :refer [touched-tiles]]
            [clojure.g2d.get-cells :refer [get-cells]]))

(defn set-touched-cells! [grid eid]
  (let [cells (get-cells grid (touched-tiles (:entity/body @eid)))]
    (assert (not-any? nil? cells))
    (swap! eid assoc :entity/touched-cells cells)
    (doseq [cell cells]
      (assert (not (get (:entities @cell) eid)))
      (swap! cell update :entities conj eid))))
