(ns moon.grid.update-potential-fields.step
  (:require [moon.cell :as cell]
            [moon.grid.cached-adjacent-cells :refer [cached-adjacent-cells]]
            [clojure.math.position.is-diagonal :refer [diagonal?]]))

; TODO performance
; * cached-adjacent-non-blocked-cells ? -> no need for cell blocked check?
; * sorted-set-by ?
; * do not refresh the potential-fields EVERY frame, maybe very 100ms & check for exists? target if they died inbetween.
; (or teleported?)
(defn f [grid faction last-marked-cells]
  (let [marked-cells (transient [])
        distance       #(cell/nearest-entity-distance % faction)
        nearest-entity #(cell/nearest-entity          % faction)
        marked? faction]
    ; sorting important because of diagonal-cell values, flow from lower dist first for correct distance
    (doseq [cell (sort-by #(distance @%) last-marked-cells)
            adjacent-cell (cached-adjacent-cells grid cell)
            :let [cell* @cell
                  adjacent-cell* @adjacent-cell]
            :when (not (or (cell/pf-blocked? adjacent-cell*)
                           (marked? adjacent-cell*)))
            :let [distance-value (+ (float (distance cell*))
                                    (float (if (diagonal? (:position cell*)
                                                          (:position adjacent-cell*))
                                             1.4 ; square root of 2 * 10
                                             1)))]]
      (swap! adjacent-cell cell/add-field-data faction distance-value (nearest-entity cell*))
      (conj! marked-cells adjacent-cell))
    (persistent! marked-cells)))
