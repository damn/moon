(ns moon.grid.set-touched-cells
  (:require [moon.body.touched-tiles :refer [touched-tiles]]
            [grid2d.get-cells :refer [get-cells]]))

(defn set-touched-cells! [grid eid]
  (let [cells (get-cells grid (touched-tiles (:entity/body @eid)))]
    (assert (not-any? nil? cells))
    (swap! eid assoc :entity/touched-cells cells)
    (doseq [cell cells]
      (assert (not (get (:entities @cell) eid)))
      (swap! cell update :entities conj eid))))
