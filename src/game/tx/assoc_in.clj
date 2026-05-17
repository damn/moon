(ns game.tx.assoc-in)

(defn do! [_ctx eid ks value]
  (swap! eid assoc-in ks value)
  nil)
