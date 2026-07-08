(ns clojure.moon.state-exit.npc-moving)

(defn f
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])
