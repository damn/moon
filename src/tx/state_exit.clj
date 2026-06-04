(ns tx.state-exit)

(defn do!
  [{:keys [ctx/k->state-exit]
    :as ctx}
   eid
   [state-k state-v]]
  (if-let [f (k->state-exit state-k)]
    (f state-v eid ctx)
    nil))
