(ns moon.grid.update-potential-fields.step
  (:require [moon.cell.is-pf-blocked :as pf-blocked?]
            [moon.cell.nearest-entity :as nearest-entity]
            [moon.cell.nearest-entity-distance :as nearest-entity-distance]
            [moon.grid.cached-adjacent-cells :refer [cached-adjacent-cells]]
            [position.is-diagonal :refer [diagonal?]]))

; TODO performance
; * cached-adjacent-non-blocked-cells ? -> no need for cell blocked check?
; * sorted-set-by ?
; * do not refresh the potential-fields EVERY frame, maybe very 100ms & check for exists? target if they died inbetween.
; (or teleported?)
(defn f [grid faction last-marked-cells]
  (let [marked-cells (transient [])
        distance       #(nearest-entity-distance/f % faction)
        nearest-entity #(nearest-entity/f          % faction)
        marked? faction]
    ; sorting important because of diagonal-cell values, flow from lower dist first for correct distance
    (doseq [cell (sort-by #(distance @%) last-marked-cells)
            adjacent-cell (cached-adjacent-cells grid cell)
            :let [cell* @cell
                  adjacent-cell* @adjacent-cell]
            :when (not (or (pf-blocked?/f adjacent-cell*)
                           (marked? adjacent-cell*)))
            :let [distance-value (+ (float (distance cell*))
                                    (float (if (diagonal? (:position cell*)
                                                          (:position adjacent-cell*))
                                             1.4 ; square root of 2 * 10
                                             1)))]]
      (swap! adjacent-cell assoc faction {:distance distance-value
                                          :eid (nearest-entity cell*)})
      (conj! marked-cells adjacent-cell))
    (persistent! marked-cells)))
