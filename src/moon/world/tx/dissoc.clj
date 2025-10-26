(ns moon.world.tx.dissoc)

(defn do!
  [_ctx eid k]
  (swap! eid dissoc k)
  nil)
