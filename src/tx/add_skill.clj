(ns tx.add-skill
  (:require [reaction-txs.add-skill :as add-skill-reaction]))

(defn do! [ctx eid {:keys [property/id] :as skill}]
  {:pre [(not (contains? (:entity/skills @eid) id))]}
  (swap! eid update :entity/skills assoc id skill)
  (add-skill-reaction/f ctx eid skill)
  nil)
