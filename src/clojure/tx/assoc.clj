(ns clojure.tx.assoc)

(defn f [_ctx eid k value]
  (swap! eid assoc k value)
  nil)
