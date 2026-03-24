(ns moon.grid)

(defn cells->entities [cells]
  (into #{} (mapcat :entities) cells))

(defprotocol Grid
  (circle->cells [g2d circle])
  (circle->entities [g2d {:keys [position radius] :as circle}])
  (cached-adjacent-cells [g2d cell])
  (point->entities [g2d position])
  (set-touched-cells! [grid eid])
  (remove-from-touched-cells! [_ eid])
  (set-occupied-cells! [grid eid])
  (remove-from-occupied-cells! [_ eid])
  (valid-position? [g2d body entity-id])
  (nearest-enemy-distance [grid entity])
  (nearest-enemy [grid entity]))
