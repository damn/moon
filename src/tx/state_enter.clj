(ns tx.state-enter)

(defn do!
  [{:keys [ctx/k->state-enter :as ctx]}
   eid
   [state-k state-v]]
  (if-let [f (k->state-enter state-k)]
    (f state-v eid)
    nil))
