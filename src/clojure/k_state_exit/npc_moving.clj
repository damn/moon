(ns clojure.k-state-exit.npc-moving)

(defn f
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])
