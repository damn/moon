(ns moon.world.tx.add-skill)

(defn do!
  [_ctx eid {:keys [property/id] :as skill}]
  {:pre [(not (contains? (:entity/skills @eid) id))]}
  (swap! eid update :entity/skills assoc id skill)
  nil)
