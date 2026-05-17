(ns game.tx.assoc)

(defn do! [_ctx eid k value]
  (swap! eid assoc k value)
  nil)
