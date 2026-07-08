(ns clojure.moon.state-exit.player-moving)

(defn f
  [_ eid _ctx]
  [[:tx/dissoc eid :entity/movement]])
