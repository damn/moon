(ns clojure.tx.add-skill)

(defn do! [ctx eid {:keys [property/id] :as skill}]
  {:pre [(not (contains? (:entity/skills @eid) id))]}
  [[:tx/update eid :entity/skills assoc id skill]
   (when (:entity/player? @eid)
     [:tx/ui-update-skill eid skill])])
