(ns moon.world.grid)

(defprotocol Grid
  (circle->cells [_ circle])
  (circle->entities [_ {:keys [position radius] :as circle}])
  (cached-adjacent-cells [_ cell])
  (point->entities [_ position])
  (set-touched-cells! [_ eid])
  (remove-from-touched-cells! [_ eid])
  (set-occupied-cells! [_ eid])
  (remove-from-occupied-cells! [_ eid])
  (valid-position? [_ body entity-id])
  (nearest-enemy-distance [_ entity])
  (nearest-enemy [_ entity]))

(defn cells->entities [cells]
  (into #{} (mapcat :entities) cells))
