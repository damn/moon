(ns moon.grid.update-potential-fields
  (:require [moon.grid.update-potential-fields.generate :refer [generate-potential-field]]))

; Assumption: The map contains no not-allowed diagonal cells, diagonal wall cells where both
; adjacent cells are walls and blocked.
; (important for wavefront-expansion and field-following)
; * entities do not move to NADs (they remove them)
; * the potential field flows into diagonals, so they should be reachable too.
;
; TODO assert somewhere/at map load no NAD's and @ potential field init & remove from
; potential-field-following the removal of NAD's.

; TODO remove max pot field movement player screen + 10 tiles as of screen size
; => is coupled to max-steps & also
; to friendly units follow player distance

(defn tick! [grid pf-cache faction entities max-iterations]
  (let [tiles->entities (let [entities (filter #(= (:entity/faction @%) faction)
                                               entities)]
                          (zipmap (map #(mapv int (:body/position (:entity/body @%))) entities)
                                  entities))
        last-state   [faction :tiles->entities]
        marked-cells [faction :marked-cells]]
    (when-not (= (get-in @pf-cache last-state) tiles->entities)
      (swap! pf-cache assoc-in last-state tiles->entities)
      (doseq [cell (get-in @pf-cache marked-cells)]
        (swap! cell dissoc faction))
      (swap! pf-cache assoc-in marked-cells (generate-potential-field
                                             grid
                                             faction
                                             tiles->entities
                                             max-iterations)))))
