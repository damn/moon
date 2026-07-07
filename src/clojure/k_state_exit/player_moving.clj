(ns clojure.k-state-exit.player-moving)

(defn f
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])
