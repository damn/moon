(ns moon.grid)

(defn cells->entities [cells]
  (into #{} (mapcat :entities) cells))

(defprotocol Grid
  (circle->cells [_ circle])
  (circle->entities [_ circle])
  (cached-adjacent-cells [_ cell])
  (point->entities [_ [x y]])
  (set-touched-cells! [_ eid])
  (remove-from-touched-cells! [_ eid])
  (set-occupied-cells! [_ eid])
  (remove-from-occupied-cells! [_ eid])
  (valid-position? [_ body entity-id])
  (nearest-enemy-distance [_ entity])
  (nearest-enemy [_ entity])
  (tick! [_ pf-cache faction entities max-iterations]))

(defprotocol NpcMovementAI
  (find-direction [_ eid]))
