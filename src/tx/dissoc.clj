(ns tx.dissoc)

(defn f [_ctx eid k]
  (swap! eid dissoc k)
  nil)
